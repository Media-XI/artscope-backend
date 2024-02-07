package com.example.codebase.controller;


import com.example.codebase.annotation.AdminOnly;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        magazineCategoryService.createCategory(name);
        return new ResponseEntity(name + " 생성 완료", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getCategories() {
        MagazineCategoryResponse.GetAll allCategory = magazineCategoryService.getAllCategory();
        return new ResponseEntity(allCategory, HttpStatus.OK);
    }

}
