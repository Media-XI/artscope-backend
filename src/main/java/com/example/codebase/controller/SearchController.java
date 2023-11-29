package com.example.codebase.controller;

import com.example.codebase.domain.agora.dto.AgorasResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionPageInfoResponseDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import com.example.codebase.domain.search.service.SearchService;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import com.example.codebase.domain.search.type.SearchSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.PositiveOrZero;

@Tag(name = "Search", description = "검색 API")
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(summary = "통합 검색", description = "[ALL] Artwork, Post, Agora, Exhibition 통합 검색", parameters = {
            @Parameter(name = "keyword", description = "검색어", required = true),
            @Parameter(name = "page", description = "페이지 번호"),
            @Parameter(name = "size", description = "페이지 사이즈"),
            @Parameter(name = "sortType", description = "정렬 방식 (정확도순, 최신순)", example = "정확도순")
    })
    @GetMapping
    public ResponseEntity totalSearch(
            @RequestParam String keyword,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "정확도순", required = false) String sortType
    ) {
        SearchSortType searchSortType = SearchSortType.create(sortType);
        Sort sort = Sort.by(Sort.Direction.DESC, searchSortType.getValue());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        SearchResponseDTO dto = searchService.totalSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "아트워크 검색", description = "아트워크 검색", parameters = {
            @Parameter(name = "keyword", description = "검색어", required = true),
            @Parameter(name = "page", description = "페이지 번호"),
            @Parameter(name = "size", description = "페이지 사이즈"),
            @Parameter(name = "sortType", description = "정렬 방식 (정확도순, 최신순)", example = "정확도순")
    })
    @GetMapping("/artwork")
    public ResponseEntity artworkSearch(
            @RequestParam String keyword,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "정확도순", required = false) String sortType
    ) {
        SearchSortType searchSortType = SearchSortType.create(sortType);
        Sort sort = Sort.by(Sort.Direction.DESC, searchSortType.getValue());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        ArtworksResponseDTO dto = searchService.artworkSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "포스트 검색", description = "포스트 검색", parameters = {
            @Parameter(name = "keyword", description = "검색어", required = true),
            @Parameter(name = "page", description = "페이지 번호"),
            @Parameter(name = "size", description = "페이지 사이즈"),
            @Parameter(name = "sortType", description = "정렬 방식 (정확도순, 최신순)", example = "정확도순")
    })
    @GetMapping("/post")
    public ResponseEntity postSearch(
            @RequestParam String keyword,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "정확도순", required = false) String sortType
    ) {
        SearchSortType searchSortType = SearchSortType.create(sortType);
        Sort sort = Sort.by(Sort.Direction.DESC, searchSortType.getValue());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        PostsResponseDTO dto = searchService.postSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "아고라 검색", description = "아고라 검색", parameters = {
            @Parameter(name = "keyword", description = "검색어", required = true),
            @Parameter(name = "page", description = "페이지 번호"),
            @Parameter(name = "size", description = "페이지 사이즈"),
            @Parameter(name = "sortType", description = "정렬 방식 (정확도순, 최신순)", example = "정확도순")
    })
    @GetMapping("/agora")
    public ResponseEntity agoraSearch(
            @RequestParam String keyword,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "정확도순", required = false) String sortType
    ) {
        SearchSortType searchSortType = SearchSortType.create(sortType);
        Sort sort = Sort.by(Sort.Direction.DESC, searchSortType.getValue());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        AgorasResponseDTO dto = searchService.agoraSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 검색", description = "이벤트 검색", parameters = {
            @Parameter(name = "keyword", description = "검색어", required = true),
            @Parameter(name = "page", description = "페이지 번호"),
            @Parameter(name = "size", description = "페이지 사이즈"),
            @Parameter(name = "sortType", description = "정렬 방식 (정확도순, 최신순)", example = "정확도순")
    })
    @GetMapping("/event")
    public ResponseEntity eventSearch(
            @RequestParam String keyword,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "정확도순", required = false) String sortType
    ) {
        SearchSortType searchSortType = SearchSortType.create(sortType);
        Sort sort = Sort.by(Sort.Direction.DESC, searchSortType.getValue());
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        ExhibitionPageInfoResponseDTO dto = searchService.eventSearch(keyword, pageRequest);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

}
