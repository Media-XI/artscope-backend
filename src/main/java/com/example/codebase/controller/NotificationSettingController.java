package com.example.codebase.controller;

import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.domain.notification.dto.NotificationSettingResponse;
import com.example.codebase.domain.notification.service.NotificationSettingService;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Tag(name = "NotificationSetting", description = "알림 설정 API")
@RequestMapping("api/notification-setting")
public class NotificationSettingController {

    private NotificationSettingService notificationSettingService;

    private MemberService memberService;

    @Autowired
    public NotificationSettingController(NotificationSettingService notificationSettingService, MemberService memberService) {
        this.notificationSettingService = notificationSettingService;
        this.memberService = memberService;
    }

    @LoginOnly
    @Operation(summary = "알림 설정 조회", description = "알림 설정을 조회합니다.")
    @GetMapping()
    public ResponseEntity getNotificationSetting() {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        NotificationSettingResponse.GetAll notificationSettingResponse = notificationSettingService.getNotificationSetting(member);

        return new ResponseEntity(notificationSettingResponse, HttpStatus.OK);
    }

    @LoginOnly
    @Operation(summary = "알림 설정 변경", description = "알림 설정을 변경합니다.")
    @PatchMapping("/{notificationType}")
    public ResponseEntity updateNotificationSetting(@PathVariable String notificationType) {
        String loginUsername = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        Member member = memberService.getEntity(loginUsername);

        notificationSettingService.updateNotificationSetting(notificationType, member);
        NotificationSettingResponse.GetAll notificationSettingResponse = notificationSettingService.getNotificationSetting(member);

        return new ResponseEntity(notificationSettingResponse, HttpStatus.OK);
    }

}
