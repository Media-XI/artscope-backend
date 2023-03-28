package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.service.ArtworkService;
import com.example.codebase.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/artwork")
public class ArtworkController {

    private final ArtworkService artworkService;

    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity createArtwork(@RequestBody ArtworkCreateDTO dto) {
        try {
            String username = SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
            ArtworkResponseDTO artwork = artworkService.createArtwork(dto, username);
            return new ResponseEntity(artwork, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity getAllArtwork() {
        try {
            List<ArtworkResponseDTO> artworks = artworkService.getAllArtwork();
            return new ResponseEntity(artworks, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
