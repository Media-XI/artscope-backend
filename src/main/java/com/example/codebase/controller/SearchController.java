package com.example.codebase.controller;

import com.example.codebase.domain.search.SearchService;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.PositiveOrZero;

@ApiOperation(value = "통합 검색", notes = "")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @ApiOperation(value = "통합 검색 API", notes = "Post, Artwork 통합 검색")
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
}
