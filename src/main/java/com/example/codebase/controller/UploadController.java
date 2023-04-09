package com.example.codebase.controller;

import com.example.codebase.s3.S3Service;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @ApiOperation(value = "파일 하나 업로드", notes = "[USER] 파일 업로드")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity upload(@RequestPart MultipartFile multipartFile) throws Exception {
        return new ResponseEntity(s3Service.saveUploadFile(multipartFile), HttpStatus.CREATED);
    }
}
