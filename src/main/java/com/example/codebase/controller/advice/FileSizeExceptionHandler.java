package com.example.codebase.controller.advice;

import com.example.codebase.controller.TestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class FileSizeExceptionHandler {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String fileSize;
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException e) {
        return new ResponseEntity( "파일 사이즈가 너무 큽니다. (최대용량사이즈: " + fileSize + ")", HttpStatus.BAD_REQUEST);
    }
}
