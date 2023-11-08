package com.example.codebase.controller;

import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.feed.dto.FeedResponseDto;
import com.example.codebase.domain.feed.service.FeedService;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @PositiveOrZero @RequestParam(defaultValue = "0") int page
    ) {
        Optional<String> loginUsername = SecurityUtil.getCurrentUsername();

        FeedResponseDto dto;
        if (loginUsername.isPresent()) {
            dto = feedService.createFeedLoginUser(loginUsername.get(), page, 10);
        } else {
            dto = feedService.createFeed(page, 10);
        }

        return new ResponseEntity(dto, HttpStatus.CREATED);
    }

    @ApiOperation(value = "최근 일주일 간 좋아요를 많이받은 Post 조회", notes = "[ALL] 최근 일주일간 좋아요를 받은 순으로 Post를 10개 조회합니다.")
    @GetMapping("/posts/like-rank")
    public ResponseEntity getPostLikeRankFromWeek() {

        List<PostResponseDTO> posts = feedService.getPostLikeRankWeek();

        return new ResponseEntity(posts, HttpStatus.OK);
    }

    @ApiOperation(value = "최근 일주일 간 좋아요를 많이받은 Artwork 조회", notes = "[ALL} 최근 일주일 간 좋아요를 많이 받은 순으로 Artwork 10개 조회")
    @GetMapping("/artworks/like-rank")
    public ResponseEntity getArtworkLikeRankFromWeek() {

        List<ArtworkResponseDTO> artworks = feedService.getArtworkLikeRankWeek();

        return new ResponseEntity(artworks, HttpStatus.OK);
    }
}
