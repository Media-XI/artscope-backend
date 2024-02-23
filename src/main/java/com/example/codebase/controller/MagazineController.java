package com.example.codebase.controller;

import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.annotation.UserOnly;
import com.example.codebase.domain.magazine.dto.MagazineCommentRequest;
import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.magazine.service.MagazineCategoryService;
import com.example.codebase.domain.magazine.service.MagazineService;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/magazines")
public class MagazineController {

    private final MagazineService magazineService;

    private final MagazineCategoryService magazineCategoryService;

    private final MemberService memberService;

    @Autowired
    public MagazineController(MagazineService magazineService, MagazineCategoryService magazineCategoryService, MemberService memberService) {
        this.magazineService = magazineService;
        this.magazineCategoryService = magazineCategoryService;
        this.memberService = memberService;
    }

    @PostMapping
    @UserOnly
    public ResponseEntity createMagazine(
            @RequestBody @NotEmpty MagazineRequest.Create magazineRequest
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);

        // 연관 객체 조회
        Member member = memberService.getEntity(loginUsername);
        MagazineCategory category = magazineCategoryService.getEntity(magazineRequest.getCategoryId());

        // 매거진 생성 로직 수행
        MagazineResponse.Get magazine = magazineService.create(magazineRequest, member, category);

        return new ResponseEntity(magazine, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity getMagazine(
            @PathVariable Long id
    ) {
        MagazineResponse.Get magazine = magazineService.get(id);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getMagazines(
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), "createdTime"));
        MagazineResponse.GetAll allMagazines = magazineService.getAll(pageRequest);

        return new ResponseEntity(allMagazines, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @UserOnly
    public ResponseEntity updateMagazine(
            @PathVariable Long id,
            @RequestBody @Valid MagazineRequest.Update magazineRequest
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(LoginRequiredException::new);
        MagazineResponse.Get magazine = magazineService.update(id, loginUsername, magazineRequest);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @UserOnly
    public ResponseEntity deleteMagazine(
            @PathVariable Long id
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        magazineService.delete(loginUsername, id);

        return new ResponseEntity("삭제 완료", HttpStatus.OK);
    }

    @Operation(summary = "매거진에 댓글 달기")
    @PostMapping("/{id}/comments")
    @LoginOnly
    public ResponseEntity newPostComment(
            @PathVariable Long id,
            @RequestBody @Valid MagazineCommentRequest.Create newComment
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        MagazineResponse.Get magazine = magazineService.newPostComment(id, member, newComment);

        return new ResponseEntity(magazine, HttpStatus.CREATED);
    }
}
