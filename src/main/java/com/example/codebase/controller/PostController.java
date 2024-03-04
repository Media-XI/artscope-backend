package com.example.codebase.controller;

import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.domain.post.dto.*;
import com.example.codebase.domain.post.service.PostService;
import com.example.codebase.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Tag(name = "Post", description = "제거 예정 , 매거진으로 이관됨")
@Deprecated
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    private final ImageService imageService;

    @Autowired
    public PostController(PostService postService, ImageService imageService) {
        this.postService = postService;
        this.imageService = imageService;
    }

    @Operation(summary = "게시글 생성", description = "[로그인] 게시글을 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createPost(
        @RequestPart(value = "dto") PostCreateDTO postCreateDTO,
        @RequestPart(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles,
        @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile) throws IOException {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        // 이미지 파일 업로드
        if (mediaFiles != null && thumbnailFile != null) {
            imageService.uploadMedias(postCreateDTO, mediaFiles);
            imageService.uploadThumbnail(postCreateDTO.getThumbnail(), thumbnailFile);
        } else {
            if (postCreateDTO.getThumbnail() != null || postCreateDTO.getMedias() != null) {
                throw new RuntimeException("미디어 파일 또는 썸네일 파일을 첨부 해주세요");
            }
        }

        PostResponseDTO post = postService.createPost(postCreateDTO, loginUsername);

        return new ResponseEntity(post, HttpStatus.CREATED);
    }

   @Operation(summary = "게시글 목록 조회", description = "[모두] 게시글 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity getPosts(@PositiveOrZero @RequestParam int page,
                                   @PositiveOrZero @RequestParam int size,
                                   @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        Optional<String> loginUsername = SecurityUtil.getCurrentUsername();

        PostsResponseDTO posts;
        if (loginUsername.isPresent()) {
            posts = postService.getPosts(loginUsername.get(), page, size, sortDirection);
        } else {
            posts = postService.getPosts(page, size, sortDirection);
        }

        return new ResponseEntity(posts, HttpStatus.OK);
    }

    @Operation(summary = "게시글 상세 조회", description = "[모두] 게시글 상세를 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity getPost(@PathVariable Long postId) {

        Optional<String> loginUsername = SecurityUtil.getCurrentUsername();

        PostWithLikesResponseDTO post;
        if (loginUsername.isPresent()) {
            post = postService.getPost(loginUsername.get(), postId);
        } else {
            post = postService.getPost(postId);
        }

        return new ResponseEntity(post, HttpStatus.OK);
    }


    @Operation(summary = "게시글 수정", description = "[로그인, 관리자] 게시글을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{postId}")
    public ResponseEntity updatePost(@PathVariable Long postId, @RequestBody PostUpdateDTO postUpdateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO post = postService.updatePost(postId, postUpdateDTO, loginUsername);

        return new ResponseEntity(post, HttpStatus.OK);
    }

    @Operation(summary = "게시글 삭제", description = "[로그인, 관리자] 게시글을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@PathVariable Long postId) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        postService.deletePost(postId, loginUsername);

        return new ResponseEntity("게시글 삭제되었습니다.", HttpStatus.OK);
    }

   @Operation(summary = "게시글 좋아요", description = "[로그인] 게시글에 좋아요를 누릅니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/like")
    public ResponseEntity likePost(@PathVariable Long postId) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO likedPost = postService.likePost(postId, loginUsername);

        if (!likedPost.getIsLiked()) {
            return new ResponseEntity(likedPost, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity(likedPost, HttpStatus.OK);
    }

    @Operation(summary = "게시글 좋아요 취소", description = "[로그인] 게시글에 좋아요를 취소합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{postId}/comments")
    public ResponseEntity createComment(@PathVariable Long postId, @RequestBody PostCommentCreateDTO commentCreateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO post = postService.createComment(postId, commentCreateDTO, loginUsername);

        return new ResponseEntity(post, HttpStatus.CREATED);
    }

    @Operation(summary = "댓글 수정", description = "[로그인, 관리자] 댓글을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity updateComment(@PathVariable Long commentId,
                                        @RequestBody PostCommentUpdateDTO commentUpdateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO post = postService.updateComment(commentId, commentUpdateDTO, loginUsername);

        return new ResponseEntity(post, HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제", description = "[로그인, 관리자] 댓글을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commentId) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        PostResponseDTO dto = postService.deleteComment(commentId, loginUsername);

        return new ResponseEntity(dto, HttpStatus.OK);
    }
}
