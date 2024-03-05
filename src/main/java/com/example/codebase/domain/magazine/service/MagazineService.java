package com.example.codebase.domain.magazine.service;

import com.example.codebase.domain.curation.repository.CurationRepository;
import com.example.codebase.domain.magazine.dto.MagazineCommentRequest;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.entity.MagazineComment;
import com.example.codebase.domain.magazine.entity.MagazineMedia;
import com.example.codebase.domain.magazine.repository.MagazineCommentRepository;
import com.example.codebase.domain.magazine.repository.MagazineMediaRepository;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MagazineService {

    private final MagazineRepository magazineRepository;

    private final MagazineCommentRepository magazineCommentRepository;

    private final MagazineMediaRepository magazineMediaRepository;

    private final CurationRepository curationRepository;

    @Autowired
    public MagazineService(MagazineRepository magazineRepository, MagazineCommentRepository magazineCommentRepository, MagazineMediaRepository magazineMediaRepository,
                           CurationRepository curationRepository) {
        this.magazineRepository = magazineRepository;
        this.magazineCommentRepository = magazineCommentRepository;
        this.magazineMediaRepository = magazineMediaRepository;
        this.curationRepository = curationRepository;
    }

    @Transactional
    public MagazineResponse.Get create(MagazineRequest.Create magazineRequest, Member member, MagazineCategory category) {
        Magazine newMagazine = Magazine.toEntity(magazineRequest, member, category);
        magazineRepository.save(newMagazine);

        List<MagazineMedia> magazineMedias = MagazineMedia.toList(magazineRequest.getMediaUrls(), newMagazine);
        magazineMediaRepository.saveAll(magazineMedias);
        return MagazineResponse.Get.from(newMagazine);
    }

    public MagazineResponse.Get get(Long id) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        magazine.incressView();

        magazineRepository.save(magazine);
        return MagazineResponse.Get.from(magazine);
    }

    public MagazineResponse.GetAll getAll(PageRequest pageRequest) {
        Page<Magazine> magazines = magazineRepository.findAll(pageRequest);
        return MagazineResponse.GetAll.from(magazines);
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

        validateOwner(loginUsername, magazine);

        magazine.update(magazineRequest);

        return MagazineResponse.Get.from(magazine);
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

        if (isMentionComment(parentComment)){
            newComment.mentionComment(parentComment);
        }
        else {
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
}
