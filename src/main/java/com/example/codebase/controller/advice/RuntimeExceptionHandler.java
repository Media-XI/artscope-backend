package com.example.codebase.controller.advice;

import com.example.codebase.exception.NotAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAccessException.class)
    public ResponseEntity handleNotAccessException(NotAccessException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN);
    }
}
