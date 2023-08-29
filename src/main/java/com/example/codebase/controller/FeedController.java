package com.example.codebase.controller;

import com.example.codebase.domain.feed.dto.FeedResponseDto;
import com.example.codebase.domain.feed.service.FeedService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.PositiveOrZero;

@ApiOperation(value = "피드", notes = "피드 관련 API")
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @ApiOperation(value = "피드 생성", notes = "[ALL] 피드를 생성합니다.")
    @PostMapping
    public ResponseEntity createFeed(
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size
            ) {
        FeedResponseDto dto = feedService.createFeed(page, size);

        return new ResponseEntity(dto, HttpStatus.CREATED);
    }
}
