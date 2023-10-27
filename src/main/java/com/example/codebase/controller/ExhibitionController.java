package com.example.codebase.controller;

import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionPageInfoDTO;
import com.example.codebase.domain.exhibition.dto.SearchExhibitionDTO;
import com.example.codebase.domain.exhibition.service.ExhibitionService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@ApiOperation(value = "공모전", notes = "공모전 관련 API") // TODO: 공모전 인가에 대해 생각해보기
@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {
  private final ExhibitionService exhibitionService;

  public ExhibitionController(ExhibitionService exhibitionService) {
    this.exhibitionService = exhibitionService;
  }

  @ApiOperation(value = "공모전 생성", notes = "공모전을 생성합니다.")
  @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
  @PostMapping
  public ResponseEntity createExhibition(@RequestBody CreateExhibitionDTO createExhibitionDTO) {
    String username = SecurityUtil.getCurrentUsername().get();
    ResponseExhibitionDTO exhibition =
        exhibitionService.createExhibition(createExhibitionDTO, username);
    return new ResponseEntity(exhibition, HttpStatus.CREATED);
  }

  @ApiOperation(value = "공모전 조회", notes = "공모전을 조회합니다.")
  @GetMapping
  public ResponseEntity getExhibition(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
      @PositiveOrZero @RequestParam int page,
      @PositiveOrZero @RequestParam int size,
      @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
    SearchExhibitionDTO searchExhibitionDTO = new SearchExhibitionDTO(startDate, endDate);
    ResponseExhibitionPageInfoDTO dtos =
        exhibitionService.getAllExhibition(searchExhibitionDTO, page, size, sortDirection);
    return new ResponseEntity(dtos, HttpStatus.OK);
  }

  @ApiOperation(value = "공모전 수정", notes = "[USER] 공모전을 수정합니다.")
  @PutMapping("/{exhibitionId}")
  public ResponseEntity updateExhibition(
      @PathVariable Long exhibitionId, @RequestBody CreateExhibitionDTO createExhibitionDTO) {
    String username = SecurityUtil.getCurrentUsername().get();
    ResponseExhibitionDTO exhibition =
        exhibitionService.updateExhibition(exhibitionId, createExhibitionDTO, username);
    return new ResponseEntity(exhibition, HttpStatus.OK);
  }

  @ApiOperation(value = "공모전 삭제", notes = "[USER] 공모전을 삭제합니다.")
  @DeleteMapping("/{exhibitionId}")
  public ResponseEntity deleteExhibition(@PathVariable Long exhibitionId) {
    String username = SecurityUtil.getCurrentUsername().get();
    exhibitionService.deleteExhibition(exhibitionId, username);
    return new ResponseEntity("공모전 삭제되었습니다.", HttpStatus.OK);
  }
}
