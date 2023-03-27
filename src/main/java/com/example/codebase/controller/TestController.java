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

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final S3Service s3Service;

    public TestController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @ApiOperation(value = "서버 상태 테스트", notes = "서버로 요청을 보내서, 정상작동하는지 확인한다.")
    @GetMapping("/ping")
    public String test() {
        return "pong!";
    }

    @ApiOperation(value = "서버 상태 테스트", notes = "서버로 요청을 보내서, 정상작동하는지 확인한다.")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/user")
    public String user() {
        return "user!";
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam MultipartFile multipartFile) throws IOException {
        try {
            return new ResponseEntity(s3Service.saveUploadFile(multipartFile), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
