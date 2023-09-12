package com.example.codebase.controller;

import com.example.codebase.domain.post.dto.PostCreateDTO;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.dto.PostUpdateDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import com.example.codebase.domain.post.service.PostService;
import com.example.codebase.util.SecurityUtil;
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
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity createPost(@RequestBody PostCreateDTO postCreateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO post = postService.createPost(postCreateDTO, loginUsername);

        return new ResponseEntity(post, HttpStatus.CREATED);
    }

    @ApiOperation(value = "게시글 전체 조회", notes = "[페이지네이션] 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity getPosts(@PositiveOrZero @RequestParam int page,
                                   @PositiveOrZero @RequestParam int size,
                                   @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        PostsResponseDTO posts = postService.getPosts(page, size, sortDirection);

        return new ResponseEntity(posts, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 상세 조회", notes = "[페이지네이션] 해당 ID의 게시글을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable Long postId) {
        PostResponseDTO post = postService.getPost(postId);

        return new ResponseEntity(post, HttpStatus.OK);
    }


    @ApiOperation(value = "게시글 수정", notes = "[로그인] 게시글을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    public ResponseEntity updatePost(@PathVariable Long postId, @RequestBody PostUpdateDTO postUpdateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO post = postService.updatePost(postId, postUpdateDTO);

        return new ResponseEntity(post, HttpStatus.OK);
    }

    @ApiOperation(value = "게시글 삭제", notes = "[로그인] 게시글을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@PathVariable Long postId) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        postService.deletePost(postId);

        return new ResponseEntity("게시글 삭제되었습니다.", HttpStatus.OK);
    }

}
