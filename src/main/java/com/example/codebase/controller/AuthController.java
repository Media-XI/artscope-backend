package com.example.codebase.controller;

import com.example.codebase.domain.auth.dto.LoginDTO;
import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.domain.auth.service.AuthService;
import com.example.codebase.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static com.example.codebase.util.SecurityUtil.getCookieAccessTokenValue;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;

    private final AuthService authService;

    public AuthController(TokenProvider tokenProvider, AuthService authService) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }

    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginDTO loginDTO) {
        TokenResponseDTO responseDTO = tokenProvider.generateToken(loginDTO);

        // Set Cookie
        return ResponseEntity.ok()
            .header("Set-Cookie", getCookieAccessTokenValue(responseDTO))
            .body(responseDTO);
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        tokenProvider.removeRefreshToken(username);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "토큰 재발급", description = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestBody String refreshToken) {
        TokenResponseDTO responseDTO = tokenProvider.regenerateToken(refreshToken);
        return ResponseEntity.ok()
            .header("Set-Cookie", getCookieAccessTokenValue(responseDTO))
            .body(responseDTO);
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
        @ApiResponse(responseCode = "400", description = "이메일 인증 실패")
    })
    @PreAuthorize("permitAll()")
    @GetMapping("/mail/authenticate")
    public ResponseEntity authenticateMailLink(
        @RequestParam String code) {
        authService.authenticateMail(code);
        return new ResponseEntity("이메일 인증되었습니다.", HttpStatus.OK);
    }
}