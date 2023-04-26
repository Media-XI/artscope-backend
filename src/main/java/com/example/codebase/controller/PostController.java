package com.example.codebase.controller;

import com.example.codebase.domain.blog.dto.PostCreateDTO;
import com.example.codebase.domain.blog.dto.PostResponseDTO;
import com.example.codebase.domain.blog.dto.PostsResponseDTO;
import com.example.codebase.domain.blog.service.PostService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.PositiveOrZero;

@RequestMapping("/api/post")
@RestController
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ApiOperation(value = "게시글 생성", notes = "[관리자 접근] 게시글을 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity createPost(@RequestBody PostCreateDTO postCreateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO post = postService.createPost(postCreateDTO, loginUsername);

        return new ResponseEntity(post ,HttpStatus.CREATED);
    }

    @ApiOperation(value = "게시글 전체 조회", notes = "[페이지네이션] 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity getPosts(@PositiveOrZero @RequestParam int page,
                                   @PositiveOrZero @RequestParam int size,
                                   @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        PostsResponseDTO posts = postService.getPosts(page, size, sortDirection);

        return new ResponseEntity(posts, HttpStatus.OK);
    }
}
