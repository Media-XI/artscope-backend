package com.example.codebase.domain.magazine.service;

import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineLike;
import com.example.codebase.domain.magazine.repository.MagazineLikeRepository;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MagazineLikeService {

    private final MagazineLikeRepository magazineLikeRepository;

    private final MagazineRepository magazineRepository;

    public MagazineLikeService(MagazineLikeRepository magazineLikeRepository, MagazineRepository magazineRepository) {
        this.magazineLikeRepository = magazineLikeRepository;
        this.magazineRepository = magazineRepository;
    }

    @Transactional
    public MagazineResponse.Get like(Long magazineId, Member member) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new NotFoundException("해당 매거진을 찾을 수 없습니다."));

        Optional<MagazineLike> like = magazineLikeRepository.findByIds(magazine, member);

        // 좋아요 로직에 대해 멱등성 보장
        if (like.isPresent()) {
            return MagazineResponse.Get.from(magazine);
        }

        MagazineLike magazineLike = MagazineLike.toEntity(magazine, member);
        magazine.addLike(magazineLike);

        magazineLikeRepository.save(magazineLike);
        return MagazineResponse.Get.from(magazine);
    }

    @Transactional
    public MagazineResponse.Get unlike(Long magazineId, Member member) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new NotFoundException("해당 매거진을 찾을 수 없습니다."));

        MagazineLike magazineLike = magazineLikeRepository.findByIds(magazine, member)
                .orElseThrow(() -> new NotFoundException("좋아요한 매거진이 아닙니다."));

        magazine.removeLike(magazineLike);

        magazineLikeRepository.delete(magazineLike);
        return MagazineResponse.Get.from(magazine);
    }
}
