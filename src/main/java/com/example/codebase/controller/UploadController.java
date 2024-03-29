package com.example.codebase.controller;

import com.example.codebase.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Upload", description = "파일 업로드 API")
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Operation(summary = "파일 업로드", description = "파일 업로드")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity upload(@RequestPart MultipartFile multipartFile) throws Exception {
        return new ResponseEntity(s3Service.saveUploadFile(multipartFile), HttpStatus.CREATED);
    }
}
