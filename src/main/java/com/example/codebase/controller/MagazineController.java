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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "매거진 API", description = "매거진 관련 API")
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

    @Operation(summary = "매거진 생성", description = "로그인한 사용자만 매거진을 생성할 수 있습니다. 매거진 생성 시 미디어 첨부 가능합니다. 또한 JSON 형식의 메타데이터를 추가할 수 있습니다.")
    @PostMapping
    @UserOnly
    public ResponseEntity createMagazine(
            @RequestBody @Valid MagazineRequest.Create magazineRequest
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);

        // 연관 객체 조회
        Member member = memberService.getEntity(loginUsername);
        MagazineCategory category = magazineCategoryService.getEntity(magazineRequest.getCategorySlug());

        // 매거진 생성 로직 수행
        MagazineResponse.Get magazine = magazineService.create(magazineRequest, member, category);

        return new ResponseEntity(magazine, HttpStatus.CREATED);
    }

    @Operation(summary = "매거진 조회")
    @GetMapping("/{id}")
    public ResponseEntity getMagazine(
            @PathVariable Long id
    ) {
        MagazineResponse.Get magazine = magazineService.get(id);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

    @Operation(summary = "매거진 목록 조회")
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

    @Operation(summary = "해당 사용자의 매거진 전체 조회", description = "해당 사용자가 작성한 매거진을 조회합니다. 페이지네이션")
    @GetMapping("/members/{username}")
    public ResponseEntity getMyMagazines(
            @PathVariable String username,
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), "createdTime"));
        Member member = memberService.getEntity(username);

        MagazineResponse.GetAll allMagazines = magazineService.getMemberMagazines(member, pageRequest);

        return new ResponseEntity(allMagazines, HttpStatus.OK);
    }

    @Operation(summary = "매거진 수정", description = "자신의 매거진만 수정 가능합니다. 기존 항목도 모두 업데이트 됩니다.")
    @PutMapping("/{id}")
    @UserOnly
    public ResponseEntity updateMagazine(
            @PathVariable Long id,
            @RequestBody @Valid MagazineRequest.Update magazineRequest
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername()
                .orElseThrow(LoginRequiredException::new);

        MagazineCategory category = magazineCategoryService.getEntity(magazineRequest.getCategorySlug());
        MagazineResponse.Get magazine = magazineService.update(id, loginUsername, magazineRequest, category);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

    @Operation(summary = "매거진 삭제")
    @DeleteMapping("/{id}")
    @UserOnly
    public ResponseEntity deleteMagazine(
            @PathVariable Long id
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        magazineService.delete(loginUsername, id);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "매거진에 댓글 달기")
    @PostMapping("/{id}/comments")
    @LoginOnly
    public ResponseEntity newMagazineComment(
            @PathVariable Long id,
            @RequestBody @Valid MagazineCommentRequest.Create newComment
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        MagazineResponse.Get magazine = magazineService.newMagazineComment(id, member, newComment);

        return new ResponseEntity(magazine, HttpStatus.CREATED);
    }

    @LoginOnly
    @Operation(summary = "매거진 댓글 수정")
    @PatchMapping("/{id}/comments/{commentId}")
    public ResponseEntity updateMagazineComment(
            @PathVariable("id") Long magazineId,
            @PathVariable("commentId") Long commentId,
            @RequestBody MagazineCommentRequest.Update updateComment
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        MagazineResponse.Get magazine = magazineService.updateMagazineComment(magazineId, commentId, updateComment, member);

        return new ResponseEntity(magazine, HttpStatus.OK);
    }

    @LoginOnly
    @Operation(summary = "매거진 댓글 삭제")
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity deleteMagazineComment(
            @PathVariable("id") Long magazineId,
            @PathVariable("commentId") Long commentId
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        MagazineResponse.Get magazine = magazineService.deleteMagazineComment(magazineId, commentId, member);

        return new ResponseEntity(magazine, HttpStatus.NO_CONTENT);
    }

    @LoginOnly
    @Operation(summary = "내가(사용자가) 팔로우 중인 유저의 매거진 목록 조회")
    @GetMapping("my/following/members")
    public ResponseEntity getFollowedMagazines(
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        String loginUsername = SecurityUtil.getCurrentUsernameValue();
        Member member = memberService.getEntity(loginUsername);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));

        MagazineResponse.GetAll allMagazines = magazineService.getFollowingMagazine(member, pageRequest);

        return new ResponseEntity(allMagazines, HttpStatus.OK);
    }
}
