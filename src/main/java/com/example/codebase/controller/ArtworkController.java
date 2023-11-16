package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.*;
import com.example.codebase.domain.artwork.service.ArtworkService;
import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@Tag(name = "Artwork", description = "아트워크 API")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    private final ImageService imageService;

    @Autowired
    public ArtworkController(ArtworkService artworkService, ImageService imageService) {
        this.artworkService = artworkService;
        this.imageService = imageService;
    }

    @Operation(summary = "아트워크 생성", description = "[USER] 아트워크를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createArtwork(
        @RequestPart(value = "dto") ArtworkCreateDTO dto,
        @RequestPart(value = "mediaFiles") List<MultipartFile> mediaFiles,
        @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile
    ) throws Exception {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (Optional.ofNullable(dto.getTags()).isPresent() && dto.getTags().size() > 5) {
            throw new RuntimeException("태그는 최대 5개까지 등록 가능합니다.");
        }

        imageService.uploadMedias(dto, mediaFiles);
        imageService.uploadThumbnail(dto.getThumbnail(), thumbnailFile);

        ArtworkResponseDTO artwork = artworkService.createArtwork(dto, username);
        return new ResponseEntity(artwork, HttpStatus.CREATED);
    }


    @Operation(summary = "아트워크 목록 조회", description = "아트워크 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity getAllArtwork(
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {

        Optional<String> username = SecurityUtil.getCurrentUsername();
        ArtworkWithLikePageDTO artworkPages = artworkService.getAllArtwork(page, size, sortDirection, username);
        return new ResponseEntity(artworkPages, HttpStatus.OK);
    }


    @Operation(summary = "아트워크 상세 조회", description = "아트워크 상세를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity getArtwork(@PathVariable Long id) {
        Optional<String> username = SecurityUtil.getCurrentUsername();
        ArtworkWithIsLikeResponseDTO artwork = artworkService.getArtwork(id, username);
        return new ResponseEntity(artwork, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 수정", description = "[USER, ADMIN] 아트워크를 수정합니다. 작성자 또는 관리자만 수정 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity updateArtwork(@PathVariable Long id, @RequestBody ArtworkUpdateDTO dto) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        if (Optional.ofNullable(dto.getTags()).isPresent() && dto.getTags().size() > 5) {
            throw new RuntimeException("태그는 최대 5개까지 등록 가능합니다.");
        }

        ArtworkResponseDTO artwork = artworkService.updateArtwork(id, dto, username);
        return new ResponseEntity(artwork, HttpStatus.OK);
    }

    /*
        현재 아트워크 목록을 받아오고
     */
    @Operation(summary = "아트워크 미디어 추가", description = "[USER, ADMIN] 아트워크에 미디어를 추가합니다. 작성자 또는 관리자만 추가 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{id}/media/{mediaId}")
    public ResponseEntity updateMediaArtwork(@PathVariable Long id,
                                             @PathVariable Long mediaId,
                                             @RequestBody ArtworkMediaCreateDTO dto) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        ArtworkResponseDTO artwork = artworkService.updateArtworkMedia(id, mediaId, dto, username);
        return new ResponseEntity(artwork, HttpStatus.OK);
    }


    @Operation(summary = "아트워크 삭제", description = "[USER, ADMIN] 아트워크를 삭제합니다. 작성자 또는 관리자만 삭제 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteArtwork(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        artworkService.deleteArtwork(id, username);

        return new ResponseEntity("아트워크가 삭제되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "아트워크 미디어 삭제", description = "[USER, ADMIN] 아트워크에 미디어를 삭제합니다. 작성자 또는 관리자만 삭제 가능합니다.")
    @GetMapping("/member/{username}")
    public ResponseEntity getUserArtworks(
        @PathVariable String username,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername()
            .orElse("");
        boolean isAuthor = loginUsername.equals(username);

        ArtworksResponseDTO artworks = artworkService.getUserArtworks(page, size, sortDirection, username, isAuthor);
        return new ResponseEntity(artworks, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 좋아요", description = "[USER] 아트워크에 좋아요를 표시합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping("/{id}/like")
    public ResponseEntity likeArtwork(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        ArtworkLikeResponseDTO artworkWithLike = artworkService.likeArtwork(id, username);

        if (!artworkWithLike.isLiked()) {
            return new ResponseEntity(artworkWithLike, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(artworkWithLike, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 좋아요 취소", description = "[USER] 아트워크에 좋아요를 취소합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @GetMapping("/member/{username}/likes")
    public ResponseEntity getUserLikeArtworks(
        @PathVariable String username,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (!SecurityUtil.isAdmin() && !loginUsername.equals(username)) {
            throw new RuntimeException("본인의 좋아요 목록만 조회 가능합니다.");
        }
        ArtworkLikeMemberPageDTO memberLikes = artworkService.getUserLikeArtworks(page, size, sortDirection, username);
        return new ResponseEntity(memberLikes, HttpStatus.OK);
    }

    @Operation(summary = "로그인한 사용자의 좋아요 아트워크 목록 조회", description = "[USER] 로그인한 사용자의 좋아요 아트워크 목록을 조회합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @GetMapping("/likes")
    public ResponseEntity getLoginUserLikeArtworks(
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        ArtworkLikeMemberPageDTO memberLikes = artworkService.getUserLikeArtworks(page, size, sortDirection,
            loginUsername);
        return new ResponseEntity(memberLikes, HttpStatus.OK);
    }


    @Operation(summary = "아트워크 좋아요 멤버 조회", description = "아트워크 좋아요 멤버를 조회합니다.")
    @GetMapping("/{id}/likes")
    public ResponseEntity getArtworkLikeMembers(
        @PathVariable Long id,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        ArtworkLikeMembersPageDTO likeMembers = artworkService.getArtworkLikeMembers(id, page, size, sortDirection);
        return new ResponseEntity(likeMembers, HttpStatus.OK);
    }

    @Operation(summary = "로그인한 사용자의 아트워크 좋아요 여부 조회", description = "[USER] 로그인한 사용자의 아트워크 좋아요 여부를 조회합니다.")
    @GetMapping("/{id}/member/likes")
    public ResponseEntity getLoginUserArtworkIsLike(
        @PathVariable Long id
    ) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        Boolean loginUserArtworkIsLike = artworkService.getLoginUserArtworkIsLiked(id, loginUsername);
        return new ResponseEntity(loginUserArtworkIsLike, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 검색", description = "아트워크를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity searchArtworks(
        @RequestParam String keyword,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        String formatKeyword = keyword.trim().replace(" ", ""); // 공백 제거
        ArtworksResponseDTO artworks = artworkService.searchArtworks(formatKeyword, page, size, sortDirection);
        return new ResponseEntity(artworks, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 댓글 생성", description = "[USER] 해당 아트워크에 댓글을 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/comments")
    public ResponseEntity commentArtwork(@PathVariable Long id, @RequestBody ArtworkCommentCreateDTO commentCreateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername()
            .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        ArtworkResponseDTO comment = artworkService.commentArtwork(id, loginUsername, commentCreateDTO);

        return new ResponseEntity(comment, HttpStatus.CREATED);
    }

    @Operation(summary = "아트워크 댓글 수정", description = "[USER, ADMIN] 해당 아트워크 댓글을 수정합니다.")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity updateArtworkComment(
        @PathVariable Long id,
        @PathVariable Long commentId,
        @RequestBody ArtworkCommentCreateDTO commentCreateDTO) {
        String loginUsername = SecurityUtil.getCurrentUsername()
            .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        ArtworkResponseDTO comment = artworkService.updateArtworkComment(id, commentId, loginUsername,
            commentCreateDTO);

        return new ResponseEntity(comment, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 댓글 삭제", description = "[USER, ADMIN] 해당 아트워크 댓글을 삭제합니다.")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity deleteArtworkComment(
        @PathVariable Long id,
        @PathVariable Long commentId) {
        String loginUsername = SecurityUtil.getCurrentUsername()
            .orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        artworkService.deleteArtworkComment(id, commentId, loginUsername);

        return new ResponseEntity("댓글이 삭제되었습니다.", HttpStatus.NO_CONTENT);
    }
}
