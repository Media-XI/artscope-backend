package com.example.codebase.controller;

import io.sentry.Sentry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test", description = "테스트 API")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "테스트", description = "테스트")
    @GetMapping("/ping")
    public String test() {
        return "pong!";
    }

    @Operation(summary = "관리자 테스트", description = "관리자 테스트")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/user")
    public String user() {
        return "user!";
    }

    @Operation(summary = "Sentry 테스트", description = "Sentry 테스트")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/sentry")
    public void sentry() {
        try {
            throw new Exception("Sentry Test Error!");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
    }
}
