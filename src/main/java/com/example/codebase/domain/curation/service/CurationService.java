package com.example.codebase.domain.curation.service;

import com.example.codebase.domain.curation.dto.CurationRequest;
import com.example.codebase.domain.curation.dto.CurationResponse;
import com.example.codebase.domain.curation.dto.CurationTime;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.curation.repository.CurationRepository;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CurationService {

    private final CurationRepository curationRepository;

    private final MagazineRepository megazineRepository;

    @Autowired
    public CurationService(CurationRepository curationRepository, MagazineRepository megazineRepository) {
        this.curationRepository = curationRepository;
        this.megazineRepository = megazineRepository;
    }

    @Transactional
    public CurationResponse.Get createCuration(CurationRequest.Create curationRequest) {
        Magazine magazine = megazineRepository.findById(curationRequest.getMagazineId())
                .orElseThrow(() -> new NotFoundException("해당 매거진이 없습니다."));

        Optional<Curation> optionalCuration = curationRepository.findByMagazine(magazine);
        Curation curation;
        if (optionalCuration.isPresent()) {
            curation = optionalCuration.get();
            curation.setUpdatedTime(); // 재 게시
        } else {
            curation = Curation.builder().build();
            curation.setMagazine(magazine);
        }

        curationRepository.save(curation);

        return CurationResponse.Get.from(curation);
    }

    @Transactional
    public void deleteCuration(Long curationId) {
        Curation curation = curationRepository.findById(curationId).orElseThrow(() -> new NotFoundException("해당 큐레이션이 존재하지 않습니다."));

        curation.delete();
    }

    @Transactional
    public CurationResponse.Get updateCuration(CurationRequest.Update curationRequest) {
        Curation curation = curationRepository.findById(curationRequest.getCurationId())
                .orElseThrow(() -> new NotFoundException("해당 큐레이션이 존재하지 않습니다."));

        Magazine magazine = megazineRepository.findById(curationRequest.getMagazineId())
                .orElseThrow(() -> new NotFoundException("해당 매거진이 존재하지 않습니다."));

        Optional<Curation> checkCuration = curationRepository.findByMagazine(magazine);
        if(checkCuration.isPresent()){
            throw new RuntimeException("이미 큐레이팅된 매거진입니다");
        }

        curation.setMagazine(magazine);

        return CurationResponse.Get.from(curation);
    }

    @Transactional(readOnly = true)
    public CurationResponse.GetAll getAllCuration(CurationTime time, PageRequest pageRequest) {
        return CurationResponse.GetAll.from(curationRepository.findByUpdatedTimeAfter(time, pageRequest));
    }


}
