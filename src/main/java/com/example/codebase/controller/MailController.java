package com.example.codebase.controller;

import com.example.codebase.domain.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@Validated
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @Operation(summary = "이메일 인증링크 전송 API", description = "해당 메일을 인증하기 위해 인증링크를 전송합니다.")
    @PreAuthorize("permitAll()")
    @PostMapping("/authenticate")
    public ResponseEntity authenticateMail(@RequestParam @Email(message = "올바른 이메일을 입력해주세요") String email) {
        mailService.sendAuthenticateMail(email);
        return new ResponseEntity("인증링크가 전송되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "아이디 찾기 메일 전송", description = "해당 이메일로 아이디를 전송합니다.")
    @GetMapping("/find-username")
    public ResponseEntity findUsername(@RequestParam @Valid @Email @NotBlank(message = "이메일은 필수입니다.") String email) {

        mailService.sendUsernameMail(email, "아이디 찾기");

        return new ResponseEntity("아이디가 담긴 메일이 전송되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "비밀번호 재설정 링크 전송", description = "비밀번호 재설정 링크를 담은 메일을 전송합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity resetPasswordMail(@RequestParam @Email(message = "올바른 이메일을 입력해주세요") String email) {
        mailService.sendPasswordResetMail(email);
        return new ResponseEntity("비밀번호 재설정 링크가 전송되었습니다.", HttpStatus.OK);
    }
}
