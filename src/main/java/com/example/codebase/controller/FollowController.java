package com.example.codebase.controller;

import com.example.codebase.domain.follow.dto.FollowMembersResponseDTO;
import com.example.codebase.domain.follow.dto.FollowRequest;
import com.example.codebase.domain.follow.service.FollowService;
import com.example.codebase.domain.notification.entity.NotificationType;
import com.example.codebase.domain.notification.service.NotificationSendService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Tag(name = "Follow", description = "팔로우 관련 API")
@Validated
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    private final NotificationSendService notificationService;

    @Autowired
    public FollowController(FollowService followService, NotificationSendService notificationService) {
        this.followService = followService;
        this.notificationService = notificationService;
    }

    @Operation(summary = "팔로우/언팔로우", description = "상대방을 팔로우/언팔로우 합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity follow(
            @RequestParam("action") @Pattern(regexp = "follow|unfollow", message = "잘못된 action 값입니다.") String action,
            @RequestBody @Valid FollowRequest.Create request
    ) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        FollowRequest.FollowEntityUrn entityUrn = FollowRequest.FollowEntityUrn.from(request.getUrn());

        if (action.equals("unfollow")) {
            return unfollow(username, entityUrn);
        }
        return follow(username, entityUrn);
    }

    private ResponseEntity follow(String username, FollowRequest.FollowEntityUrn entityUrn) {
        switch (entityUrn) {
            case MEMBER -> {
                checkSameMember(username, entityUrn.getId(), "팔로잉");
                followService.followMember(username, entityUrn.getId());
                notificationService.send(username, entityUrn.getId(), NotificationType.NEW_FOLLOWER);
            }
            case TEAM -> {
                followService.followTeam(username, entityUrn.getId());
            }
        }
        return new ResponseEntity("팔로잉 했습니다.", HttpStatus.CREATED);
    }

    private ResponseEntity unfollow(String username, FollowRequest.FollowEntityUrn entityUrn) {
        switch (entityUrn) {
            case MEMBER -> {
                checkSameMember(username, entityUrn.getId(), "언팔로우");
                followService.unfollowMember(username, entityUrn.getId());
            }
            case TEAM -> {
                followService.unfollowTeam(username, entityUrn.getId());
            }
        }
        return new ResponseEntity("언팔로잉 했습니다.", HttpStatus.OK);
    }

    private void checkSameMember(String username1, String username2, String message) {
        if (username1.equals(username2)) {
            throw new RuntimeException("자기 자신을 %s 할 수 없습니다".formatted(message));
        }
    }

    @Operation(summary = "팔로잉", description = "해당 유저가 팔로잉 하는 사람 목록을 조회 합니다")
    @GetMapping("/{username}/following")
    public ResponseEntity getFollowingList(@PathVariable("username") String username,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int page,
                                           @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        Optional<String> loginUsername = SecurityUtil.getCurrentUsername();
        PageRequest pageRequest = PageRequest.of(page, size);
        FollowMembersResponseDTO followingListResponse = followService.getFollowingList(loginUsername, username, pageRequest);

        return new ResponseEntity(followingListResponse, HttpStatus.OK);
    }


    @Operation(summary = "팔로워", description = "해당 유저를 팔로워 하는 사람 목록을 조회 합니다")
    @GetMapping("{username}/follower")
    public ResponseEntity getFollowerList(@PathVariable("username") String username,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") int page,
                                          @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        Optional<String> loginUsername = SecurityUtil.getCurrentUsername();
        PageRequest pageRequest = PageRequest.of(page, size);
        FollowMembersResponseDTO followingListResponse = followService.getFollowerList(loginUsername, username, pageRequest);

        return new ResponseEntity(followingListResponse, HttpStatus.OK);
    }


}
