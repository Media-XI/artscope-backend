package com.example.codebase.controller;

import com.example.codebase.domain.follow.service.FollowService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Follow", description = "팔로우 관련 API")
@Validated
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @Operation(summary = "팔로우", description = "상대방을 팔로우합니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{username}")
    public ResponseEntity followMember(@PathVariable("username") String followUser) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        followService.followMember(username, followUser);

        return new ResponseEntity("팔로우 했습니다.", HttpStatus.CREATED);
    }

    @Operation(summary = "언팔로우" , description = "상대방을 언팔로우합니다")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/{username}")
    public ResponseEntity unfollowMember(@PathVariable("username") String followUser) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        followService.unfollowMember(username, followUser);

        return new ResponseEntity("언팔로우 했습니다.", HttpStatus.NO_CONTENT);
    }

}
