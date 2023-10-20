package com.example.codebase.domain.artwork.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.dto.*;
import com.example.codebase.domain.artwork.entity.*;
import com.example.codebase.domain.artwork.repository.ArtworkCommentRepository;
import com.example.codebase.domain.artwork.repository.ArtworkLikeMemberRepository;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotAccessException;
import com.example.codebase.exception.NotFoundException;
import com.example.codebase.s3.S3Service;
import com.example.codebase.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtworkService {
    private final ArtworkRepository artworkRepository;
    private final ArtworkCommentRepository artworkCommentRepository;
    private final ArtworkLikeMemberRepository artworkLikeMemberRepository;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;

    @Autowired
    public ArtworkService(ArtworkRepository artworkRepository, ArtworkCommentRepository artworkCommentRepository, ArtworkLikeMemberRepository artworkLikeMemberRepository, S3Service s3Service, MemberRepository memberRepository) {
        this.artworkRepository = artworkRepository;
        this.artworkCommentRepository = artworkCommentRepository;
        this.artworkLikeMemberRepository = artworkLikeMemberRepository;
        this.s3Service = s3Service;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ArtworkResponseDTO createArtwork(ArtworkCreateDTO dto, String username) {

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Artwork artwork = Artwork.of(dto, member);

        // 썸네일 추가
        ArtworkMedia thumbnail = ArtworkMedia.of(dto.getThumbnail(), artwork);
        artwork.addArtworkMedia(thumbnail); // 제일 첫번째는 썸네일로

        for (ArtworkMediaCreateDTO mediaCreateDTO : dto.getMedias()) {
            ArtworkMedia media = ArtworkMedia.of(mediaCreateDTO, artwork);
            artwork.addArtworkMedia(media);
        }

        member.addArtwork(artwork);
        Artwork saveArtwork = artworkRepository.save(artwork);
        return ArtworkResponseDTO.from(saveArtwork);
    }

    public ArtworkWithLikePageDTO getAllArtwork(int page, int size, String sortDirection, Optional<String> username) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        if (username.isPresent()) {
            Member member = memberRepository.findByUsername(username.get())
                    .orElseThrow(NotFoundMemberException::new);
            Page<ArtworkWithIsLike> artworksWithIsLikePage;

            if (SecurityUtil.isAdmin()) {
                // 관리자면 공개여부와 상관없이 전체 조회
                artworksWithIsLikePage = artworkRepository.findAllWithIsLikeByMember(member, pageRequest);
            }
            else {
                artworksWithIsLikePage = artworkRepository.findAllWithIsLikeByMemberAndVisible(member, true, pageRequest);
            }

            PageInfo pageInfo = PageInfo.of(page, size, artworksWithIsLikePage.getTotalPages(), artworksWithIsLikePage.getTotalElements());

            List<ArtworkWithIsLikeResponseDTO> dtos = artworksWithIsLikePage.stream()
                    .map(ArtworkWithIsLikeResponseDTO::from)
                    .collect(Collectors.toList());

            return ArtworkWithLikePageDTO.of(dtos, pageInfo);
        }

        Page<Artwork> artworksPage = artworkRepository.findAll(pageRequest);

        PageInfo pageInfo = PageInfo.of(page, size, artworksPage.getTotalPages(), artworksPage.getTotalElements());
        List<ArtworkWithIsLikeResponseDTO> dtos = artworksPage.stream()
                .map(ArtworkWithIsLikeResponseDTO::from)
                .collect(Collectors.toList());

        return ArtworkWithLikePageDTO.of(dtos, pageInfo);
    }

    @Transactional
    public ArtworkWithIsLikeResponseDTO getArtwork(Long id, Optional<String> username) {
        boolean existLike = false;
        // 사용자 좋아요 여부에 따른 해당 아트워크 좋아요 조회
        if (username.isPresent()) {
            existLike = artworkLikeMemberRepository.existsByArtwork_IdAndMember_Username(id, username.get());
        }
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 작품을 찾을 수 없습니다."));

        artwork.increaseView(); // 조회수 증가

        // 달린 댓글 추가
        List<ArtworkCommentResponseDTO> comments = getComments(artwork);
        return ArtworkWithIsLikeResponseDTO.from(artwork, comments, existLike);
    }

    @Transactional
    public ArtworkResponseDTO updateArtwork(Long id, ArtworkUpdateDTO dto, String username) {
        Artwork artwork = artworkRepository.findByIdAndMember_Username(id, username)
                .orElseThrow(() -> new NotAccessException("해당 작품의 소유자가 아닙니다."));

        artwork.update(dto);
        return ArtworkResponseDTO.from(artwork);
    }

    @Transactional
    public void deleteArtwork(Long id, String username) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 작품을 찾을 수 없습니다."));

        if (username != null && !artwork.getMember().getUsername().equals(username)) {
            throw new NotAccessException("해당 작품의 소유자가 아닙니다.");
        }

        // Artwork의 Artwork Media URL를 가져와서 S3 Object Delete
        List<ArtworkMedia> artworkMedias = artwork.getArtworkMedia();
        deleteS3Objects(artworkMedias);

        artworkRepository.delete(artwork);
    }

    public void deleteS3Object(List<ArtworkMedia> artworkMedias) {
        for (ArtworkMedia artworkMedia : artworkMedias) {
            s3Service.deleteObject(artworkMedia.getMediaUrl());
        }
    }

    public void deleteS3Objects(List<ArtworkMedia> artworkMedias) {
        List<String> urls = artworkMedias.stream()
                .map(ArtworkMedia::getMediaUrl)
                .collect(Collectors.toList());
        s3Service.deleteObjects(urls);
    }


    public ArtworkResponseDTO updateArtworkMedia(Long id, Long mediaId, ArtworkMediaCreateDTO dto, String username) {
        Artwork artwork = artworkRepository.findByIdAndMember_Username(id, username)
                .orElseThrow(() -> new NotAccessException("해당 작품의 소유자가 아닙니다."));
        artwork.updateArtworkMedia(mediaId, dto);
        return ArtworkResponseDTO.from(artwork);
    }

    public ArtworksResponseDTO getUserArtworks(int page, int size, String sortDirection, String username, boolean isAuthor) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Artwork> artworks = artworkRepository.findAllByMember_UsernameAndVisible(pageRequest, username, !isAuthor);
        PageInfo pageInfo = PageInfo.of(page, size, artworks.getTotalPages(), artworks.getTotalElements());

        List<ArtworkResponseDTO> dtos = artworks.stream()
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());

        return ArtworksResponseDTO.of(dtos, pageInfo);
    }

    @Transactional
    public ArtworkLikeResponseDTO likeArtwork(Long id, String username) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 작품을 찾을 수 없습니다."));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Optional<ArtworkLikeMember> likeMemberOptional = artworkLikeMemberRepository.findById(new ArtworkLikeMemberId(member.getId(), artwork.getId()));
        String status = "좋아요";
        if (likeMemberOptional.isPresent()) {
            // 좋아요 해제
            artworkLikeMemberRepository.delete(likeMemberOptional.get());
            status = "좋아요 취소";
        } else {
            // 좋아요 추가
            ArtworkLikeMember artworkLikeMember = ArtworkLikeMember.of(artwork, member);
            artwork.addArtworkLikeMember(artworkLikeMember);
        }

        Integer Likes = artworkLikeMemberRepository.countByArtworkId(artwork.getId());
        artwork.setLikes(Likes);

        return ArtworkLikeResponseDTO.from(artwork, !likeMemberOptional.isPresent(), status);
    }

    public ArtworkLikeMemberPageDTO getUserLikeArtworks(int page, int size, String sortDirection, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "likedTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ArtworkLikeMember> memberLikeArtworks = artworkLikeMemberRepository.findAllByMemberId(member.getId(), pageRequest);
        PageInfo pageInfo = PageInfo.of(page, size, memberLikeArtworks.getTotalPages(), memberLikeArtworks.getTotalElements());

        List<ArtworkLikeMemberResponseDTO> dtos = memberLikeArtworks.stream()
                .map(ArtworkLikeMemberResponseDTO::from)
                .collect(Collectors.toList());

        return ArtworkLikeMemberPageDTO.of(dtos, pageInfo);
    }

    @Transactional(readOnly = true)
    public ArtworkLikeMembersPageDTO getArtworkLikeMembers(Long id, int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "likedTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<ArtworkLikeMember> artworkLikeMembers = artworkLikeMemberRepository.findAllByArtworkId(id, pageRequest);
        PageInfo pageInfo = PageInfo.of(page, size, artworkLikeMembers.getTotalPages(), artworkLikeMembers.getTotalElements());

        List<String> usernames = artworkLikeMembers.stream()
                .map(artworkLikeMember -> artworkLikeMember.getMember().getUsername())
                .collect(Collectors.toList());

        return ArtworkLikeMembersPageDTO.from(usernames, artworkLikeMembers.getTotalElements(), pageInfo);
    }

    public Boolean getLoginUserArtworkIsLiked(Long id, String loginUsername) {
        Member member = memberRepository.findByUsername(loginUsername)
                .orElseThrow(NotFoundMemberException::new);

        Optional<ArtworkLikeMember> byArtworkIdAndMember = artworkLikeMemberRepository.findByArtworkIdAndMember(id, member);

        return byArtworkIdAndMember.isPresent();

    }

    @Transactional(readOnly = true)
    public ArtworksResponseDTO searchArtworks(String keyword, int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Artwork> artworks = artworkRepository.findAllByKeywordContaining(keyword, pageRequest);
        PageInfo pageInfo = PageInfo.of(page, size, artworks.getTotalPages(), artworks.getTotalElements());

        List<ArtworkResponseDTO> dtos = artworks.stream()
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());

        return ArtworksResponseDTO.of(dtos, pageInfo);
    }

    @Transactional
    public ArtworkResponseDTO commentArtwork(Long artworkId, String loginUsername, ArtworkCommentCreateDTO commentCreateDTO) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new NotFoundException("해당 작품을 찾을 수 없습니다."));

        Member member = memberRepository.findByUsername(loginUsername)
                .orElseThrow(NotFoundMemberException::new);

        ArtworkComment artworkComment = ArtworkComment.of(commentCreateDTO, artwork, member);
        artworkCommentRepository.save(artworkComment);

        artwork.addArtworkComment(artworkComment);
        
        //artwork.addArtworkComment(artworkComment);

        List<ArtworkCommentResponseDTO> comments = getComments(artwork);
        return ArtworkResponseDTO.of(artwork, comments);
    }

    private List<ArtworkCommentResponseDTO> getComments(Artwork artwork) {
        return artwork.getArtworkComments().stream()
                .map(ArtworkCommentResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteArtworkComment(Long artworkId, Long commentId, String loginUsername) {
        ArtworkComment comment = artworkCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 댓글을 찾을 수 없습니다."));

        Member member = memberRepository.findByUsername(loginUsername)
                .orElseThrow(NotFoundMemberException::new);

        if (!comment.getAuthor().equals(member)) {
            throw new NotAccessException("해당 댓글의 작성자가 아닙니다.");
        }

        Artwork artwork = comment.getArtwork();

        if (!artwork.getId().equals(artworkId)) {
            throw new NotFoundException("해당 작품을 찾을 수 없습니다.");
        }

        artwork.removeArtworkComment(comment);
        artworkRepository.save(artwork);
        artworkCommentRepository.delete(comment);
    }

//    @Transactional
//    public ArtworkResponseDTO addChildComment(Long artworkId, Long commentId, String loginUsername, ArtworkCommentCreateDTO commentCreateDTO) {
//        ArtworkComment parentComment = artworkCommentRepository.findById(commentId)
//                .orElseThrow(() -> new NotFoundException("해당 댓글을 찾을 수 없습니다."));
//
//        Artwork artwork = parentComment.getArtwork();
//        if (!artwork.getId().equals(artworkId)) {
//            throw new NotFoundException("해당 작품을 찾을 수 없습니다.");
//        }
//
//        Member member = memberRepository.findByUsername(loginUsername)
//                .orElseThrow(NotFoundMemberException::new);
//
//        ArtworkComment childComment = ArtworkComment.of(commentCreateDTO, artwork, member);
//        parentComment.addChildComment(childComment);
//
//        List<ArtworkCommentResponseDTO> comments = getComments(parentComment);
//        return ArtworkResponseDTO.of(artwork, comments);
//    }
//
//    private List<ArtworkCommentResponseDTO> getComments(ArtworkComment parentComment) {
//        return parentComment.getChildComments().stream()
//                .map(ArtworkCommentResponseDTO::from)
//                .collect(Collectors.toList());
//    }

}
