package com.example.codebase.controller;

import com.example.codebase.s3.S3Service;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@ApiOperation(value = "테스트", notes = "테스트 관련 API")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @ApiOperation(value = "서버 상태 테스트", notes = "서버로 요청을 보내서, 정상작동하는지 확인한다.")
    @GetMapping("/ping")
    public String test() {
        return "pong!";
    }

    @ApiOperation(value = "사용자 권한 테스트", notes = "사용자 권한이 있는지 확인한다.")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/user")
    public String user() {
        return "user!";
    }

    @ApiOperation(value = "커밋 테스트", notes = "커밋 테스트")
    @GetMapping("/commit")
    public String commitTest() {
        return "commit test!";
    }
}
