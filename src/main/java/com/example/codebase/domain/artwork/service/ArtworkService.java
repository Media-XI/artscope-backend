package com.example.codebase.domain.artwork.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.dto.*;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.repository.ArtworkMediaRepository;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotAccessException;
import com.example.codebase.exception.NotFoundException;
import com.example.codebase.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtworkService {
    private final ArtworkRepository artworkRepository;
    private final ArtworkMediaRepository artworkMediaRepository;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;

    @Autowired
    public ArtworkService(ArtworkRepository artworkRepository, ArtworkMediaRepository artworkMediaRepository, S3Service s3Service, MemberRepository memberRepository) {
        this.artworkRepository = artworkRepository;
        this.artworkMediaRepository = artworkMediaRepository;
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

        Artwork saveArtwork = artworkRepository.save(artwork);
        return ArtworkResponseDTO.from(saveArtwork);
    }

    public ArtworksResponseDTO getAllArtwork(int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Artwork> artworksPage = artworkRepository.findAll(pageRequest);

        PageInfo pageInfo = PageInfo.of(page, size, artworksPage.getTotalPages(), artworksPage.getTotalElements());
        List<ArtworkResponseDTO> dtos = artworksPage.stream()
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());
        return ArtworksResponseDTO.of(dtos, pageInfo);
    }

    public ArtworkResponseDTO getArtwork(Long id) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 작품을 찾을 수 없습니다."));
        return ArtworkResponseDTO.from(artwork);
    }

    public ArtworkResponseDTO updateArtwork(Long id, ArtworkUpdateDTO dto, String username) {
        Artwork artwork = artworkRepository.findByIdAndMember_Username(id, username)
                .orElseThrow(() -> new NotAccessException("해당 작품의 소유자가 아닙니다."));

        artwork.update(dto);
        return ArtworkResponseDTO.from(artwork);
    }
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

    public ArtworksResponseDTO getUserArtworks(int page, int size, String sortDirection, String username) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Artwork> artworks = artworkRepository.findAllByMember_Username(pageRequest, username);
        PageInfo pageInfo = PageInfo.of(page, size, artworks.getTotalPages(), artworks.getTotalElements());

        List<ArtworkResponseDTO> dtos = artworks.stream()
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());

        return ArtworksResponseDTO.of(dtos, pageInfo);
    }
}
