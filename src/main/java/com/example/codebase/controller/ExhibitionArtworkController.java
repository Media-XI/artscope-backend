package com.example.codebase.controller;

import com.example.codebase.domain.exhibition.service.ExhibitionService;
import com.example.codebase.domain.exhibition_artwork.dto.ExhibitionArtworkResponseDTO;
import com.example.codebase.domain.exhibition_artwork.dto.ExhibitionArtworksResponseDTO;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionArtworkController {

    private final ExhibitionService exhibitionService;

    public ExhibitionArtworkController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @ApiOperation(value = "공모전에 아트워크 제출", notes = "[USER] 사용자가 공모전에 작품을 제출합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping("/{exhibitionId}/artworks/{artworkId}")
    public ResponseEntity addArtworkToExhibition(@PathVariable Long exhibitionId, @PathVariable Long artworkId) {
        String username = SecurityUtil.getCurrentUsername().get();
        ExhibitionArtworkResponseDTO exhibitionArtworkResponseDTO = exhibitionService.addArtworkToExhibition(exhibitionId, artworkId, username);
        return new ResponseEntity(exhibitionArtworkResponseDTO, HttpStatus.CREATED);
    }

    @ApiOperation(value = "해당 공모전에 제출한 아트워크 조회", notes = "공모전에 등록된 작품을 조회합니다.")
    @GetMapping("/{exhibitionId}/artworks")
    public ResponseEntity getExhibitionWithArtworks(@PathVariable Long exhibitionId) {
        ExhibitionArtworksResponseDTO exhibitionArtworks = exhibitionService.getArtworkFromExhibition(exhibitionId);
        return new ResponseEntity(exhibitionArtworks, HttpStatus.OK);
    }

    @ApiOperation(value = "공모전에 제출한 아트워크 상태 수정", notes = "[ADMIN] 공모전에 제출한 작품의 상태를 수정합니다. (submitted, pending, accepted, rejected)")
    @PutMapping("/{exhibitionId}/artworks/{artworkId}")
    public ResponseEntity updateExhibitionArtwork(@PathVariable Long exhibitionId,
                                                  @PathVariable Long artworkId,
                                                  @RequestParam String status) {
        if (!SecurityUtil.isAdmin()) {
            throw new RuntimeException();
        }

        ExhibitionArtworkResponseDTO exhibitionArtworkResponseDTO = exhibitionService.updateStatusExhibitionArtwork(exhibitionId, artworkId, status);
        return new ResponseEntity(exhibitionArtworkResponseDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "공모전에 제출한 아트워크 삭제", notes = "[USER] 공모전에 제출 작품을 삭제합니다.")
    @DeleteMapping("/{exhibitionId}/artworks/{artworkId}")
    public ResponseEntity deleteExhibitionArtwork(@PathVariable Long exhibitionId, @PathVariable Long artworkId) {
        String username = SecurityUtil.getCurrentUsername().get();
        exhibitionService.deleteExhibitionArtwork(exhibitionId, artworkId, username);
        return new ResponseEntity("제출한 아트워크 삭제되었습니다. ",HttpStatus.OK);
    }


}
