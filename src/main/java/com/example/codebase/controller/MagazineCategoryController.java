package com.example.codebase.controller;


import com.example.codebase.annotation.AdminOnly;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "매거진 카테고리 API", description = "매거진 카테고리 관련 API")
@RestController
@RequestMapping("/api/magazine-category")
public class MagazineCategoryController {

    private final MagazineCategoryService magazineCategoryService;

    public MagazineCategoryController(MagazineCategoryService magazineCategoryService) {
        this.magazineCategoryService = magazineCategoryService;
    }

    @PostMapping
    @AdminOnly
    public ResponseEntity createCategory(String name) {
        MagazineCategoryResponse.Get category = magazineCategoryService.createCategory(name);
        return new ResponseEntity(category, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getCategories() {
        MagazineCategoryResponse.GetAll allCategory = magazineCategoryService.getAllCategory();
        return new ResponseEntity(allCategory, HttpStatus.OK);
    }

}
