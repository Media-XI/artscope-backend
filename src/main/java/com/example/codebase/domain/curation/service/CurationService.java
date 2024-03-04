package com.example.codebase.domain.curation.service;

import com.example.codebase.domain.curation.dto.CurationRequest;
import com.example.codebase.domain.curation.dto.CurationResponse;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.curation.repository.CurationRepository;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.repository.MagazineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CurationResponse.Get createCuration(CurationRequest.Create curationRequest){
        Curation curation = curationRepository.findEmptyOrOldCuration();

        Magazine magazine = megazineRepository.findById(curationRequest.getMagazineId())
                .orElseThrow(() -> new RuntimeException("해당 매거진이 없습니다."));

        curation.setMagazine(magazine);

        return CurationResponse.Get.from(curation);
    }

    @Transactional
    public void deleteCuration(Long curationId){
        Curation curation = curationRepository.findById(curationId).orElseThrow(() -> new RuntimeException("해당 큐레이션이 존재하지 않습니다."));

        curation.clearMagazine();
    }

    @Transactional
    public CurationResponse.Get updateCuration(CurationRequest.Update curationRequest) {
        Curation curation = curationRepository.findById(curationRequest.getCurationId())
                .orElseThrow(() -> new RuntimeException("해당 큐레이션이 존재하지 않습니다."));

        Magazine megazine = megazineRepository.findById(curationRequest.getMagazineId()).orElseThrow(() -> new RuntimeException("해당 메거진이 존재하지 않습니다."));

        curation.setMagazine(megazine);

        return CurationResponse.Get.from(curation);
    }

    @Transactional
    public CurationResponse.GetAll getAllCuration(){
        return CurationResponse.GetAll.from(curationRepository.findAllCuration());
    }


}
