package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.*;
import com.example.codebase.domain.artwork.service.ArtworkService;
import com.example.codebase.s3.S3Service;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@ApiOperation(value = "아트워크", notes = "아트워크 관련 API")
@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    private final S3Service s3Service;

    public ArtworkController(ArtworkService artworkService, S3Service s3Service) {
        this.artworkService = artworkService;
        this.s3Service = s3Service;
    }

    @ApiOperation(value = "아트워크 생성", notes = "[USER] 아트워크를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity createArtwork(
            @RequestPart(value = "dto") ArtworkCreateDTO dto,
            @RequestPart(value = "mediaFiles") List<MultipartFile> mediaFiles
    ) throws Exception {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        if (mediaFiles.size() > 5) {
            throw new RuntimeException("파일은 최대 5개까지 업로드 가능합니다.");
        }

        if (mediaFiles.size() == 0) {
            throw new RuntimeException("파일을 업로드 해주세요.");
        }

        int i = 0;
        for (ArtworkMediaCreateDTO mediaDto : dto.getMedias()) {
            String savedUrl = s3Service.saveUploadFile(mediaFiles.get(i++));
            mediaDto.setMediaUrl(savedUrl);
        }

        ArtworkResponseDTO artwork = artworkService.createArtwork(dto, username);
        return new ResponseEntity(artwork, HttpStatus.CREATED);
    }

    @ApiOperation(value = "아트워크 조회", notes = "아트워크 전체 조회합니다. \n 정렬 : ASC 오름차순, DESC 내림차순")
    @GetMapping
    public ResponseEntity getAllArtwork(@PositiveOrZero @RequestParam int page,
                                        @PositiveOrZero @RequestParam int size,
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
            @PositiveOrZero @RequestParam int page,
            @PositiveOrZero @RequestParam int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection,
            @PathVariable String username) {
        ArtworksResponseDTO artworks = artworkService.getUserArtworks(page, size, sortDirection, username);
        return new ResponseEntity(artworks, HttpStatus.OK);
    }
}
