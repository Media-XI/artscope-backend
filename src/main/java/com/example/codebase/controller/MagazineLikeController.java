package com.example.codebase.controller;

import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
import com.example.codebase.domain.magazine.service.MagazineLikeService;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "매거진 좋아요 API", description = "매거진 좋아요 관련 API")
@RestController
@RequestMapping("/api/magazines")
public class MagazineLikeController {

    private final MagazineLikeService magazineLikeService;

    private final MemberService memberService;

    public MagazineLikeController(MagazineLikeService magazineLikeService, MemberService memberService) {
        this.magazineLikeService = magazineLikeService;
        this.memberService = memberService;
    }

    @LoginOnly
    @PostMapping("/{magazineId}/like")
    public ResponseEntity like(
            @PathVariable Long magazineId
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        MagazineResponse.Get magazine = magazineLikeService.like(magazineId, member);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

    @LoginOnly
    @PostMapping("/{magazineId}/unlike")
    public ResponseEntity unlike(
            @PathVariable Long magazineId
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        MagazineResponse.Get magazine = magazineLikeService.unlike(magazineId, member);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

}
