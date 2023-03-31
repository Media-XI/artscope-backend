package com.example.codebase.controller;

import com.example.codebase.s3.S3Service;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@ApiOperation(value = "파일 업로드 API", notes = "파일 업로드")
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @ApiOperation(value = "파일 업로드", notes = "파일 업로드")
    @PostMapping
    public ResponseEntity upload(@RequestParam MultipartFile multipartFile) throws IOException {
        return new ResponseEntity(s3Service.saveUploadFile(multipartFile), HttpStatus.CREATED);
    }
}
