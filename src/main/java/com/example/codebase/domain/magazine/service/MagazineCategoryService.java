package com.example.codebase.domain.magazine.service;

import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.repository.MagazineCategoryRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MagazineCategoryService {

    private final MagazineCategoryRepository magazineCategoryRepository;

    public MagazineCategoryService(MagazineCategoryRepository magazineCategoryRepository) {
        this.magazineCategoryRepository = magazineCategoryRepository;
    }

    public MagazineCategoryResponse.Get createCategory(MagazineCategoryRequest.Create request) {
        MagazineCategory parentCategory = findParentCategory(request.getParentId());

        if (parentCategory != null) {
            parentCategory.checkDepth();
        }

        checkCategoryExists(request, parentCategory);

        MagazineCategory category = MagazineCategory.toEntity(request.getName(), request.getSlug(), parentCategory);
        magazineCategoryRepository.save(category);
        return MagazineCategoryResponse.Get.from(category);
    }

    private MagazineCategory findParentCategory(Long parentId) throws NotFoundException {
        if (parentId == null) {
            return null;
        }
        return magazineCategoryRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));
    }

    private void checkCategoryExists(MagazineCategoryRequest.Create request, MagazineCategory parentCategory) {
        boolean exists = magazineCategoryRepository.existsByNameAndParent(request.getName(), parentCategory);
        if (exists) {
            throw new RuntimeException("이미 이름이나 슬러그가 중복되는 카테고리가 존재합니다.");
        }
    }

    @Transactional(readOnly = true)
    public MagazineCategoryResponse.GetAll getAllCategory() {
        List<MagazineCategory> all = magazineCategoryRepository.findAll();
        return MagazineCategoryResponse.GetAll.from(all);
    }

    @Transactional(readOnly = true)
    public MagazineCategoryResponse.GetAll getSubCategories(String slug) {
        List<MagazineCategory> subCategories = magazineCategoryRepository.findBySlug(slug);
        return MagazineCategoryResponse.GetAll.from(subCategories);
    }

    @Transactional(readOnly = true)
    public MagazineCategory getEntity(Long categoryId) {
        return magazineCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));
    }

    public void deleteCategory(Long categoryId) {
        MagazineCategory category = magazineCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));

        category.delete();
    }

    public MagazineCategoryResponse.Get updateCategory(Long categoryId, MagazineCategoryRequest.Update request) {
        MagazineCategory category = magazineCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("해당 카테고리가 존재하지 않습니다."));

        Optional.ofNullable(request.getParentId()).ifPresent(parentId -> {
            MagazineCategory parentCategory = magazineCategoryRepository.findById(parentId)
                    .orElseThrow(() -> new NotFoundException("부모 카테고리가 존재하지 않습니다."));
            parentCategory.checkDepth();
            category.update(parentCategory);
        });

        category.update(request);
        magazineCategoryRepository.save(category);
        return MagazineCategoryResponse.Get.from(category);
    }
}
