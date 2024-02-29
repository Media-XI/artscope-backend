package com.example.codebase.domain.magazine.service;

import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.repository.MagazineCategoryRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MagazineCategoryService {

    private final MagazineCategoryRepository magazineCategoryRepository;

    public MagazineCategoryService(MagazineCategoryRepository magazineCategoryRepository) {
        this.magazineCategoryRepository = magazineCategoryRepository;
    }

    public MagazineCategoryResponse.Get createCategory(String name) {
        MagazineCategory category = MagazineCategory.toEntity(name);
        magazineCategoryRepository.save(category);
        return MagazineCategoryResponse.Get.from(category);
    }

    public MagazineCategoryResponse.GetAll getAllCategory() {
        List<MagazineCategory> all = magazineCategoryRepository.findAll();
        return MagazineCategoryResponse.GetAll.from(all);
    }

    public MagazineCategory getEntity(Long categoryId) {
        return magazineCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));
    }
}