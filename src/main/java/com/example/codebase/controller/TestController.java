package com.example.codebase.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @ApiOperation(value = "서버 상태 테스트" , notes = "서버로 요청을 보내서, 정상작동하는지 확인한다.")
    @GetMapping("/ping")
    public String test() {
        return "pong!";
    }

    @ApiOperation(value = "서버 상태 테스트" , notes = "서버로 요청을 보내서, 정상작동하는지 확인한다.")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/user")
    public String user() {
        return "user!";
    }
}
