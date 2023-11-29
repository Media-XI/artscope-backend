package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.search.SearchService;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

@Tag(name = "Search", description = "검색 API")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(summary = "전체 검색", description = "[ALL] 전체 검색")
    @GetMapping
    public ResponseEntity totalSearch(
        @RequestParam String keyword,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        SearchResponseDTO dto = searchService.totalSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 검색", description = "아트워크 검색")
    @GetMapping("/artwork")
    public ResponseEntity artworkSearch(
        @RequestParam String keyword,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "DESC", required = false) String sortDirection
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        ArtworksResponseDTO dto = searchService.artworkSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }
}
