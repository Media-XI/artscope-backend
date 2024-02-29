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
                .orElseThrow(() -> new NotFoundException("해당 좋아요를 찾을 수 없습니다."));

        magazine.removeLike(magazineLike);

        magazineLikeRepository.delete(magazineLike);
        return MagazineResponse.Get.from(magazine);
    }
}
