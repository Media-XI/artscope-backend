package com.example.codebase.controller;

import com.example.codebase.domain.mail.service.MailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @ApiOperation(value = "이메일 인증링크 전송 API", notes = "해당 메일을 인증하기 위해 인증링크를 전송합니다.")
    @PreAuthorize("permitAll()")
    @PostMapping()
    public ResponseEntity authenticateMail(@RequestParam String email) {
        mailService.sendMail(email);
        // TODO 가입 후 이메일 인증으로 변경
        // TODO 이메일 인증 후에는 로그인이 가능하도록 변경 -> 무분별한 인증 전송을 방지하기 위해
        return new ResponseEntity("", HttpStatus.OK);
    }



}
