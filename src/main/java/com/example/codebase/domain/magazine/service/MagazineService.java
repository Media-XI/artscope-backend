package com.example.codebase.domain.magazine.service;

import com.example.codebase.domain.magazine.dto.MagazineCommentRequest;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.*;
import com.example.codebase.domain.magazine.repository.MagazineCategoryRepository;
import com.example.codebase.domain.magazine.repository.MagazineCommentRepository;
import com.example.codebase.domain.magazine.repository.MagazineMediaRepository;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.repository.TeamRepository;
import com.example.codebase.exception.NotFoundException;
import com.example.codebase.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MagazineService {

    private final MagazineRepository magazineRepository;

    private final MagazineCommentRepository magazineCommentRepository;

    private final MagazineMediaRepository magazineMediaRepository;

    private final MagazineCategoryRepository magazineCategoryRepository;


    @Autowired
    public MagazineService(MagazineRepository magazineRepository, MagazineCommentRepository magazineCommentRepository, MagazineMediaRepository magazineMediaRepository, MagazineCategoryRepository magazineCategoryRepository) {
        this.magazineRepository = magazineRepository;
        this.magazineCommentRepository = magazineCommentRepository;
        this.magazineMediaRepository = magazineMediaRepository;
        this.magazineCategoryRepository = magazineCategoryRepository;
    }

    @Transactional
    public MagazineResponse.Get createMagazine(MagazineRequest.Create magazineRequest, Member member, MagazineCategory category, @Nullable Team team) {
        Magazine newMagazine = Magazine.toEntity(magazineRequest, member, category, team);

        magazineRepository.save(newMagazine);

        List<MagazineMedia> magazineMedias = MagazineMedia.toList(magazineRequest.getMediaUrls(), newMagazine);
        magazineMediaRepository.saveAll(magazineMedias);

        magazineRepository.save(newMagazine);
        return MagazineResponse.Get.from(newMagazine);
    }

    @Transactional
    public MagazineResponse.Get get(Long id) {
        MagazineWithIsLiked magazine = magazineRepository.findMagazineWithIsLiked(id, SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        magazine.getMagazine().incressView();

        magazineRepository.save(magazine.getMagazine());
        return MagazineResponse.Get.from(magazine);
    }

    public MagazineResponse.GetAll getAll(PageRequest pageRequest) {
        Page<MagazineWithIsLiked> magazines = magazineRepository.findAllMagazineWithIsLiked(pageRequest, SecurityUtil.getLoginUsername());
        return MagazineResponse.GetAll.withLike(magazines);
    }

    @Transactional
    public void delete(String loginUsername, Long id) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        validateOwner(loginUsername, magazine);

        magazine.delete();
    }

    @Transactional
    public MagazineResponse.Get update(Long id, String loginUsername, MagazineRequest.Update magazineRequest) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        MagazineCategory magazineCategory = getCategory(magazineRequest.getCategorySlug(), magazine.getCategory());

        validateOwner(loginUsername, magazine);

        magazine.update(magazineRequest, magazineCategory);

        return MagazineResponse.Get.from(magazine);
    }

    private MagazineCategory getCategory(String categorySlug, MagazineCategory defaultCategory) {
        if (categorySlug != null) {
            return magazineCategoryRepository.findBySlug(categorySlug)
                    .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));
        }
        return defaultCategory;
    }

    private void validateOwner(String loginUsername, Magazine magazine) {
        if (!magazine.isOwner(loginUsername)) {
            throw new IllegalArgumentException("해당 매거진의 소유자가 아닙니다.");
        }
    }

    @Transactional
    public MagazineResponse.Get newMagazineComment(Long magazineId, Member member, MagazineCommentRequest.Create newCommentDto) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        MagazineComment newComment = MagazineComment.toEntity(newCommentDto, member, magazine);

        if (isReplyComment(newCommentDto)) {
            replyComment(newComment, newCommentDto.getParentCommentId());
        }

        magazineCommentRepository.save(newComment);
        magazineRepository.save(magazine);
        return MagazineResponse.Get.from(magazine);
    }

    private boolean isReplyComment(MagazineCommentRequest.Create newCommentDto) {
        return newCommentDto.getParentCommentId() != null;
    }

    private void replyComment(MagazineComment newComment, Long parentCommentId) {
        MagazineComment parentComment = magazineCommentRepository.findByIdAndMagazine(parentCommentId, newComment.getMagazine())
                .orElseThrow(() -> new NotFoundException("부모댓글이 존재하지 않거나 해당 매거진에 작성된 댓글이 아닙니다."));

        if (isMentionComment(parentComment)) {
            newComment.mentionComment(parentComment);
        } else {
            newComment.setParentComment(parentComment);
        }
    }

    private boolean isMentionComment(MagazineComment parentComment) {
        return parentComment.hasParentComment();
    }

    @Transactional
    public MagazineResponse.Get updateMagazineComment(Long magazineId, Long commentId, MagazineCommentRequest.Update updateComment, Member member) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        MagazineComment magazineComment = magazineCommentRepository.findByIdAndMagazine(commentId, magazine)
                .orElseThrow(() -> new NotFoundException("댓글이 존재하지 않거나 해당 매거진에 작성된 댓글이 아닙니다."));

        validateCommentAuthor(magazineComment, member);

        magazineComment.update(updateComment);

        return MagazineResponse.Get.from(magazine);
    }

    @Transactional
    public MagazineResponse.Get deleteMagazineComment(Long magazineId, Long commentId, Member member) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        MagazineComment magazineComment = magazineCommentRepository.findByIdAndMagazine(commentId, magazine)
                .orElseThrow(() -> new NotFoundException("댓글이 존재하지 않거나 해당 매거진에 작성된 댓글이 아닙니다."));

        validateCommentAuthor(magazineComment, member);

        magazineComment.delete();

        return MagazineResponse.Get.from(magazine);
    }

    private void validateCommentAuthor(MagazineComment magazineComment, Member member) {
        if (!magazineComment.isCommentAuthor(member)) {
            throw new RuntimeException("댓글 작성자가 아닙니다.");
        }
    }

    public MagazineResponse.GetAll getMemberMagazines(Member member, PageRequest pageRequest) {
        Page<Magazine> magazines = magazineRepository.findByMember(member, pageRequest);
        return MagazineResponse.GetAll.from(magazines);
    }

    public MagazineResponse.GetAll getFollowingMagazine(Member member, PageRequest pageRequest) {
        Page<Magazine> magazines = magazineRepository.findByMemberToFollowing(member, pageRequest);
        return MagazineResponse.GetAll.from(magazines);
    }

}
