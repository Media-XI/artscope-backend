package com.example.codebase.domain.magazine.service;

import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MagazineService {

    private final MagazineRepository magazineRepository;

    @Autowired
    public MagazineService(MagazineRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    @Transactional
    public MagazineResponse.Get create(MagazineRequest.Create magazineRequest, Member member, MagazineCategory category) {
        Magazine newMagazine = Magazine.toEntity(magazineRequest, member, category);
        magazineRepository.save(newMagazine);
        return MagazineResponse.Get.from(newMagazine);
    }

    public MagazineResponse.Get get(Long id) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));
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

        isOwner(loginUsername, magazine);

        magazineRepository.deleteById(id);
    }

    @Transactional
    public MagazineResponse.Get update(Long id, String loginUsername, MagazineRequest.Update magazineRequest) {
        Magazine magazine = magazineRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        isOwner(loginUsername, magazine);

        magazine.update(magazineRequest);
        magazineRepository.save(magazine);
        return MagazineResponse.Get.from(magazine);
    }

    private void isOwner(String loginUsername, Magazine magazine) {
        if (!magazine.isOwner(loginUsername)) {
            throw new IllegalArgumentException("해당 매거진의 소유자가 아닙니다.");
        }
    }
}
