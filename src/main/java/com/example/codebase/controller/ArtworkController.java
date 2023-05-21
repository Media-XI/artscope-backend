package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.*;
import com.example.codebase.domain.artwork.service.ArtworkService;
import com.example.codebase.s3.S3Service;
import com.example.codebase.util.FileUtil;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApiOperation(value = "아트워크", notes = "아트워크 관련 API")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    private final S3Service s3Service;

    @Value("${app.file-count}")
    private String fileCount;

    public ArtworkController(ArtworkService artworkService, S3Service s3Service) {
        this.artworkService = artworkService;
        this.s3Service = s3Service;
    }

    @ApiOperation(value = "아트워크 생성", notes = "[USER] 아트워크를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createArtwork(
            @RequestPart(value = "dto") ArtworkCreateDTO dto,
            @RequestPart(value = "mediaFiles") List<MultipartFile> mediaFiles,
            @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile
    ) throws Exception {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (mediaFiles.size() > Integer.valueOf(fileCount)) {
            throw new RuntimeException("파일은 최대 " + fileCount + "개까지 업로드 가능합니다.");
        }

        if (mediaFiles.size() == 0) {
            throw new RuntimeException("파일을 업로드 해주세요.");
        }

        if (Optional.ofNullable(dto.getTags()).isPresent() && dto.getTags().size() > 5) {
            throw new RuntimeException("태그는 최대 5개까지 등록 가능합니다.");
        }

        if (!dto.getThumbnail().getMediaType().equals("image") && FileUtil.validateImageFile(thumbnailFile.getInputStream())) {
            throw new RuntimeException("썸네일은 이미지 파일만 업로드 가능합니다.");
        } else {
            // 썸네일 파일 이미지 사이즈 구하기
            BufferedImage bufferedImage = FileUtil.getBufferedImage(thumbnailFile.getInputStream());
            dto.getThumbnail().setImageSize(bufferedImage);
            // 썸네일 업로드
            String savedUrl = s3Service.saveUploadFile(thumbnailFile);
            dto.getThumbnail().setMediaUrl(savedUrl);
        }

        for (int i = 0; i < dto.getMedias().size(); i++) {
            ArtworkMediaCreateDTO mediaDto = dto.getMedias().get(i);

            if (mediaDto.getMediaType().equals("url")) {
                String youtubeUrl = new String(mediaFiles.get(i).getBytes(), "UTF-8");

                if (!youtubeUrl.matches("^(https?\\:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.?be)\\/.+$")) {
                    throw new RuntimeException("유튜브 링크 형식이 올바르지 않습니다. ex) https://www.youtube.com/watch?v=XXXXXXXXXXX 또는 https://youtu.be/XXXXXXXXXXX");
                }

                mediaDto.setMediaUrl(youtubeUrl);
            } else {
                // 이미지 파일이면 원본 이미지의 사이즈를 구합니다.
                if (mediaDto.getMediaType().equals("image")) {
                    BufferedImage bufferedImage = FileUtil.getBufferedImage(mediaFiles.get(i).getInputStream());
                    mediaDto.setImageSize(bufferedImage);
                }
                // 파일 업로드
                String savedUrl = s3Service.saveUploadFile(mediaFiles.get(i));
                mediaDto.setMediaUrl(savedUrl);
            }
        }

        ArtworkResponseDTO artwork = artworkService.createArtwork(dto, username);
        return new ResponseEntity(artwork, HttpStatus.CREATED);
    }

    @ApiOperation(value = "아트워크 전체 조회", notes = "아트워크 전체 조회합니다. \n 정렬 : ASC 오름차순, DESC 내림차순")
    @GetMapping
    public ResponseEntity getAllArtwork(
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        ArtworksResponseDTO responseDTO = artworkService.getAllArtwork(page, size, sortDirection);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "ID로 아트워크 조회", notes = "해당 ID의 아트워크를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity getArtwork(@PathVariable Long id) {
        ArtworkResponseDTO artwork = artworkService.getArtwork(id);
        return new ResponseEntity(artwork, HttpStatus.OK);
    }

    @ApiOperation(value = "아트워크 수정", notes = "[USER] 아트워크를 수정합니다. 작성자만 수정 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
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
    @ApiOperation(value = "아트워크 미디어 수정", notes = "[USER] 아트워크의 미디어를 수정합니다. 작성자만 수정 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PutMapping("/{id}/media/{mediaId}")
    public ResponseEntity updateMediaArtwork(@PathVariable Long id,
                                             @PathVariable Long mediaId,
                                             @RequestBody ArtworkMediaCreateDTO dto) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        ArtworkResponseDTO artwork = artworkService.updateArtworkMedia(id, mediaId, dto, username);
        return new ResponseEntity(artwork, HttpStatus.OK);
    }


    @ApiOperation(value = "아트워크 삭제", notes = "[USER, ADMIN] 아트워크를 삭제합니다. 작성자 또는 관리자만 삭제 가능합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteArtwork(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (SecurityUtil.isAdmin()) {
            username = null;
        }

        artworkService.deleteArtwork(id, username);
        return new ResponseEntity("아트워크가 삭제되었습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "사용자의 아트워크 조회", notes = "사용자의 아트워크를 조회합니다.")
    @GetMapping("/member/{username}")
    public ResponseEntity getUserArtworks(
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection,
            @PathVariable String username) {
        ArtworksResponseDTO artworks = artworkService.getUserArtworks(page, size, sortDirection, username);
        return new ResponseEntity(artworks, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @ApiOperation(value = "아트워크 좋아요", notes = "해당 아트워크의 좋아요를 누릅니다. (좋아요는 토글 방식입니다)")
    @PostMapping("/{id}/like")
    public ResponseEntity likeArtwork(@PathVariable Long id) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        ArtworkLikeResponseDTO artworkWithLike = artworkService.likeArtwork(id, username);
        return new ResponseEntity(artworkWithLike, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @ApiOperation(value = "해당 사용자의 좋아요한 아트워크 전체 조회", notes = "해당 사용자가 좋아요한 아트워크 전체를 조회합니다. 좋아요한 시간순으ㅉ 정렬합니다. \n 정렬 : ASC 오름차순, DESC 내림차순")
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

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @ApiOperation(value = "해당 아트워크의 좋아요 표시한 사용자들 조회", notes = "해당 아트워크의 좋아요 표시한 사용자들을 조회합니다.")
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

}
