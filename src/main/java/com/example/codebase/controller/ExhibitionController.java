package com.example.codebase.controller;

import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.service.ExhibitionService;
import com.example.codebase.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exhibition")
public class ExhibitionController {
    private final ExhibitionService exhibitionService;

    public ExhibitionController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity createExhibition(@RequestBody CreateExhibitionDTO createExhibitionDTO) {
        try {
            String username = SecurityUtil.getCurrentUsername().get();
            ResponseExhibitionDTO exhibition = exhibitionService.createExhibition(createExhibitionDTO, username);
            return new ResponseEntity(exhibition, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity getExhibition() {
        try {
            List<ResponseExhibitionDTO> dtos = exhibitionService.getAllExhibition();
            return new ResponseEntity(dtos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
