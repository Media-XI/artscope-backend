package com.example.codebase.controller;

import static com.example.codebase.util.SecurityUtil.getCookieAccessTokenValue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.codebase.domain.auth.dto.LoginDTO;
import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.domain.auth.service.AuthService;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.domain.member.service.MemberService;
import com.example.codebase.jwt.TokenProvider;
import com.example.codebase.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;

    private final AuthService authService;

    private final MemberService memberService;

    public AuthController(TokenProvider tokenProvider, AuthService authService, MemberService memberService) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
        this.memberService = memberService;
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

    @Operation(summary = "내 프로필 조회", description = "[USER] 내 프로필을 조회합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @GetMapping("/auth/me")
    public ResponseEntity getProfile() {
        String username = SecurityUtil.getCurrentUsernameValue();
        MemberResponseDTO member = memberService.getProfile(username);
        return new ResponseEntity(member, HttpStatus.OK);
    }
}
