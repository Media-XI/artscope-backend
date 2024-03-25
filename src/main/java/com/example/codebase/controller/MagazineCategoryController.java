package com.example.codebase.controller;


import com.example.codebase.annotation.AdminOnly;
import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity createCategory(@RequestBody @Valid MagazineCategoryRequest.Create request) {
        MagazineCategoryResponse.Create category = magazineCategoryService.createCategory(request);
        return new ResponseEntity(category, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getAllCategories() {
        MagazineCategoryResponse.GetAll allCategory = magazineCategoryService.getAllCategory();
        return new ResponseEntity(allCategory, HttpStatus.OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity getSubCategories(@PathVariable String slug) {
        MagazineCategoryResponse.GetAll subCategory = magazineCategoryService.getSubCategories(slug);
        return new ResponseEntity(subCategory, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    @AdminOnly
    public ResponseEntity deleteCategory(@PathVariable Long categoryId) {
        magazineCategoryService.deleteCategory(categoryId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{categoryId}")
    @AdminOnly
    public ResponseEntity updateCategory(@PathVariable Long categoryId, @RequestBody @Valid MagazineCategoryRequest.Update request) {
        MagazineCategoryResponse.Get category = magazineCategoryService.updateCategory(categoryId, request);
        return new ResponseEntity(category, HttpStatus.OK);
    }

}
