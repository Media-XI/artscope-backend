package com.example.codebase.controller;

import com.example.codebase.domain.exhibition.dto.CreateEventScheduleDTO;
import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionIntroduceDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionPageInfoDTO;
import com.example.codebase.domain.exhibition.dto.SearchExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.UpdateExhibitionDTO;
import com.example.codebase.domain.exhibition.service.ExhibitionService;
import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@ApiOperation(value = "이벤트", notes = "이벤트 관련 API") // TODO: 공모전 인가에 대해 생각해보기
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

  @ApiOperation(value = "이벤트 생성", notes = "[ADMIN, CURATOR, ARTIST] 이벤트를 생성합니다.")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity createExhibition(
      @RequestPart(value = "dto") @Valid CreateExhibitionDTO dto,
      @RequestPart(value = "mediaFiles") List<MultipartFile> mediaFiles,
      @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile)
      throws Exception {
    String username =
        SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

    imageService.mediasUpload(dto, mediaFiles);
    imageService.thumbnailUpload(dto.getThumbnail(), thumbnailFile);

    ResponseExhibitionDTO exhibition = exhibitionService.createExhibition(dto, username);

    return new ResponseEntity(exhibition, HttpStatus.CREATED);
  }

  @ApiOperation(value = "개별 이벤트 일정 추가 ", notes = "[ADMIN, CURATOR, ARTIST] 이벤트 일정을 개별로 추가합니다.")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
  @PostMapping("/{exhibitionId}/schedule")
  public ResponseEntity createEventSchedule(
      @PathVariable Long exhibitionId, @RequestBody @Valid CreateEventScheduleDTO dto)
      throws Exception {
    String username =
        SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

    exhibitionService.createEventSchedule(exhibitionId, dto, username);

    return new ResponseEntity("이벤트 일정이 추가되었습니다.", HttpStatus.CREATED);
  }

  @ApiOperation(value = "이벤트 기간으로 전체 조회", notes = "해당 기간 범위 안에 이벤트 전체 조회합니다.")
  @GetMapping
  public ResponseEntity getExhibition(
      @RequestBody @Valid SearchExhibitionDTO searchExhibitionDTO,
      @PositiveOrZero @RequestParam int page,
      @PositiveOrZero @RequestParam int size,
      @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
    ResponseExhibitionPageInfoDTO dtos =
        exhibitionService.getAllExhibition(searchExhibitionDTO, page, size, sortDirection);
    return new ResponseEntity(dtos, HttpStatus.OK);
  }

  @ApiOperation(value = "이벤트 상세 조회", notes = "이벤트를 상세 조회합니다.")
  @GetMapping("/{exhibitionId}")
  public ResponseEntity getExhibitionDetail(@PathVariable Long exhibitionId) {
    ResponseExhibitionIntroduceDTO exhibition = exhibitionService.getExhibitionDetail(exhibitionId);
    return new ResponseEntity(exhibition, HttpStatus.OK);
  }

  @ApiOperation(value = "이벤트 수정", notes = "[ADMIN, CURATOR, ARTIST] 이벤트를 수정합니다.")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
  @PutMapping("/{exhibitionId}")
  public ResponseEntity updateExhibition(
      @PathVariable Long exhibitionId, @RequestBody @Valid UpdateExhibitionDTO dto)
      throws Exception {
    String username =
        SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

    ResponseExhibitionIntroduceDTO exhibition =
        exhibitionService.updateExhibition(exhibitionId, dto, username);

    return new ResponseEntity(exhibition, HttpStatus.OK);
  }

  @ApiOperation(value = "이벤트 삭제", notes = "[ADMIN, ARTIST, CURATOR] 해당 이벤트를 삭제합니다.")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
  @DeleteMapping("/{exhibitionId}")
  public ResponseEntity deleteExhibition(@PathVariable Long exhibitionId) {
    String username =
        SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
    exhibitionService.deleteExhibition(exhibitionId, username);
    return new ResponseEntity("이벤트가 삭제되었습니다.", HttpStatus.OK);
  }

  @ApiOperation(value = "이벤트 일정 개별 삭제 ", notes = "[ADMIN, CURATOR, ARTIST] 해당 이벤트 일정을 개별로 삭제합니다.")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
  @DeleteMapping("/{exhibitionId}/schedule/{eventScheduleId}")
  public ResponseEntity deleteEventSchedule(
      @PathVariable Long exhibitionId, @PathVariable Long eventScheduleId) {
    String username =
        SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
    exhibitionService.deleteEventSchedule(exhibitionId, eventScheduleId, username);
    return new ResponseEntity("이벤트 일정이 삭제되었습니다.", HttpStatus.OK);
  }

  @ApiOperation(value = "이벤트 일정 전체 삭제", notes = "[ADMIN, CURATOR, ARTIST] 해당 이벤트 일정을 전체 삭제합니다. ")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_CURATOR')")
  @DeleteMapping("/{exhibitionId}/all")
  public ResponseEntity deleteAllExhibition(@PathVariable Long exhibitionId) {
    String username =
        SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
    exhibitionService.deleteAllEventSchedules(exhibitionId, username);
    return new ResponseEntity("이벤트 일정이 전체 삭제되었습니다.", HttpStatus.OK);
  }
}
