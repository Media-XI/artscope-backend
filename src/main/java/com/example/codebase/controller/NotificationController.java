package com.example.codebase.controller;

import com.example.codebase.annotation.AdminOnly;
import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.notification.dto.NotificationMessageRequest;
import com.example.codebase.domain.notification.dto.NotificationResponse;
import com.example.codebase.domain.notification.entity.Notification;
import com.example.codebase.domain.notification.service.NotificationSendService;
import com.example.codebase.domain.notification.service.NotificationService;
import com.example.codebase.domain.notification.service.NotificationEventService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Notification", description = "알림 API")
@RequestMapping("api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    private final MemberService memberService;
    private final NotificationEventService notificationEventService;

    private final NotificationSendService notificationSendService;

    @Autowired
    public NotificationController(NotificationService notificationService, MemberService memberService, NotificationEventService notificationEventService
            , NotificationSendService notificationSendService) {
        this.notificationService = notificationService;
        this.memberService = memberService;
        this.notificationEventService = notificationEventService;
        this.notificationSendService = notificationSendService;
    }

    @AdminOnly
    @Operation(summary = "알림 생성하기", description = "[ADMIN] 알림을 생성합니다.")
    @PostMapping
    public ResponseEntity sendAdminNotification(@Valid @RequestBody NotificationMessageRequest notificationMessageRequest) {
        notificationMessageRequest.validAdminNotificationType();

        Map.Entry<List<Member>, Notification>  result = notificationService.createNotification(notificationMessageRequest, notificationMessageRequest.getNotificationType());

        List<Member> members = result.getKey();
        Notification notification = result.getValue();
        notificationSendService.send(members, notification);

        return new ResponseEntity("알림이 생성되었습니다", HttpStatus.CREATED);
    }

    @LoginOnly
    @Operation(summary = "알림 목록 보기", description = "알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity getNotificationList(
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "20") int size) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        PageRequest pageRequest = PageRequest.of(page, size);
        NotificationResponse.GetAll notifications = notificationService.getNotificationList(member, pageRequest);

        return new ResponseEntity(notifications, HttpStatus.OK);
    }

    @LoginOnly
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음으로 처리합니다.")
    @PutMapping("/{notificationId}")
    public ResponseEntity readNotification(@PathVariable Long notificationId) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        NotificationResponse.Get notification = notificationService.readNotification(member, notificationId);

        return new ResponseEntity(notification, HttpStatus.OK);
    }

    @LoginOnly
    @Operation(summary = "알림 전체 읽음 처리", description = "모든 알림을 읽음으로 처리합니다.")
    @PutMapping
    public ResponseEntity readAllNotification() {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        NotificationResponse.GetAll notifications = notificationService.readAllNotification(member);
        return new ResponseEntity(notifications, HttpStatus.OK);
    }

    @LoginOnly
    @Operation(summary = "알림 삭제", description = "알림을 삭제합니다.")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity deleteNotification(@PathVariable Long notificationId) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        notificationService.deleteNotification(member, notificationId);

        return new ResponseEntity("알림이 삭제되었습니다", HttpStatus.NO_CONTENT);
    }

    @LoginOnly
    @Operation(summary = "알림 전체 삭제", description = "알림을 전체 삭제합니다.")
    @DeleteMapping
    public ResponseEntity deleteAllNotification() {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        notificationService.deleteAllNotification(member);

        return new ResponseEntity("알림이 전체 삭제되었습니다", HttpStatus.NO_CONTENT);
    }

    @LoginOnly
    @Operation(summary = "알림 sse 연결", description = "알림 sse 연결")
    @GetMapping(value = "/sse-connect", produces = "text/event-stream")
    public SseEmitter subScribe() {
        String loginUserName = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUserName);

        return notificationEventService.connection(member);
    }

    @LoginOnly
    @Operation(summary = "알림 sse 연결 해제", description = "알림 sse 연결 해제")
    @GetMapping(value = "/sse-disconnect")
    public ResponseEntity unSubScribe() {
        String loginUserName = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUserName);

        notificationEventService.disconnection(member);

        return new ResponseEntity("알림 SSE 연결 종료", HttpStatus.OK);
    }

}
