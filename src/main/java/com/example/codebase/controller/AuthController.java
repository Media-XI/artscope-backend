package com.example.codebase.controller;

import com.example.codebase.domain.auth.dto.LoginDTO;
import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.jwt.TokenProvider;
import com.example.codebase.util.RedisUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@ApiOperation(value = "인증", notes = "인증 관련 API")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenProvider tokenProvider;

    public AuthController(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @ApiOperation(value = "로그인", notes = "로그인")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        TokenResponseDTO responseDTO = tokenProvider.generateToken(loginDTO);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestBody String refreshToken) {
        TokenResponseDTO responseDTO = tokenProvider.regenerateToken(refreshToken);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }
}