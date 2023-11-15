package com.example.codebase.controller;

import com.example.codebase.domain.exhibition.dto.*;
import com.example.codebase.domain.exhibition.service.ExhibitionService;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Tag(name = "Exhibition", description = "전시회 API")
@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    private final ImageService imageService;

    @Autowired
    public ExhibitionController(ExhibitionService exhibitionService, ImageService imageService) {
        this.exhibitionService = exhibitionService;
        this.imageService = imageService;
    }

    @Operation(summary = "이벤트 생성", description = "[ADMIN, CURATOR, ARTIST] 이벤트를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createExhibition(
        @RequestPart(value = "dto") @Valid ExhbitionCreateDTO dto,
        @RequestPart(value = "mediaFiles") List<MultipartFile> mediaFiles,
        @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile)
        throws Exception {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        imageService.uploadMedias(dto, mediaFiles);
        imageService.uploadThumbnail(dto.getThumbnail(), thumbnailFile);

        ExhibitionDetailResponseDTO exhibition = exhibitionService.createExhibition(dto, username);

        return new ResponseEntity(exhibition, HttpStatus.CREATED);
    }

    @Operation(summary = "이벤트 일정 생성", description = "[ADMIN, CURATOR, ARTIST] 이벤트 일정을 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
    @PostMapping("/{exhibitionId}/schedule")
    public ResponseEntity createEventSchedule(
        @PathVariable Long exhibitionId, @RequestBody @Valid EventScheduleCreateDTO dto)
        throws Exception {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        exhibitionService.createEventSchedule(exhibitionId, dto, username);

        return new ResponseEntity("이벤트 일정이 추가되었습니다.", HttpStatus.CREATED);
    }

    @Operation(summary = "이벤트 목록 조회", description = "이벤트 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity getExhibition(
        @ModelAttribute @Valid ExhibitionSearchDTO exhibitionSearchDTO,
        @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        ExhibitionPageInfoResponseDTO dtos =
            exhibitionService.getAllExhibition(exhibitionSearchDTO, page, size, sortDirection);
        return new ResponseEntity(dtos, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 상세를 조회합니다.")
    @GetMapping("/{exhibitionId}")
    public ResponseEntity getExhibitionDetail(@PathVariable Long exhibitionId) {
        ExhibitionDetailResponseDTO exhibition = exhibitionService.getExhibitionDetail(exhibitionId);
        return new ResponseEntity(exhibition, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 수정", description = "[ADMIN, CURATOR, ARTIST] 이벤트를 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
    @PutMapping("/{exhibitionId}")
    public ResponseEntity updateExhibition(
        @PathVariable Long exhibitionId, @RequestBody @Valid ExhibitionUpdateDTO dto)
        throws Exception {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        ExhibitionDetailResponseDTO exhibition =
            exhibitionService.updateExhibition(exhibitionId, dto, username);

        return new ResponseEntity(exhibition, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 일정 수정", description = "[ADMIN, CURATOR, ARTIST] 이벤트 일정을 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
    @DeleteMapping("/{exhibitionId}")
    public ResponseEntity deleteExhibition(@PathVariable Long exhibitionId) {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        exhibitionService.deleteExhibition(exhibitionId, username);
        return new ResponseEntity("이벤트가 삭제되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "이벤트 일정 전체 조회", description = "이벤트 일정 전체를 조회합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
    @DeleteMapping("/{exhibitionId}/schedule/{eventScheduleId}")
    public ResponseEntity deleteEventSchedule(
        @PathVariable Long exhibitionId, @PathVariable Long eventScheduleId) {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        exhibitionService.deleteEventSchedule(exhibitionId, eventScheduleId, username);
        return new ResponseEntity("이벤트 일정이 삭제되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "이벤트 일정 전체 삭제", description = "이벤트 일정 전체를 삭제합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
    @DeleteMapping("/{exhibitionId}/all")
    public ResponseEntity deleteAllExhibition(@PathVariable Long exhibitionId) {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        exhibitionService.deleteAllEventSchedules(exhibitionId, username);
        return new ResponseEntity("이벤트 일정이 전체 삭제되었습니다.", HttpStatus.OK);
    }
}
