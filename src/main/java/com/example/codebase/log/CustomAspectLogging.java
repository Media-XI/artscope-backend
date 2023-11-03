package com.example.codebase.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CustomAspectLogging {

    @AfterReturning(value = "within(com.example.codebase.controller.advice.CustomExceptionHandler)", returning = "response")
    public void logResponseException(JoinPoint joinPoint, Object response) {
        if (response instanceof ResponseEntity) {
            ResponseEntity res = (ResponseEntity) response;
            log.info("Advice Method : {}", joinPoint.getSignature().toString());
            // log.info("Advice Error : {}", joinPoint.toLongString());
            log.info("Advice Response : {}", res.getBody());
        } else {
            log.info("Advice Method : {}", joinPoint.getSignature().toString());
            // log.info("Advice Error : {}", joinPoint.getTarget());
            log.info("Advice Response : {}", response);
        }
    }

    @AfterThrowing(value = "within(com.example.codebase.*)", throwing = "throwable")
    public void logResponseException(JoinPoint joinPoint, Throwable throwable) {
        log.info("Advice Exception : ", throwable);
    }

    @AfterReturning(value = "within(com.example.codebase.controller.advice.FileSizeExceptionHandler)", returning = "response")
    public void logResponseFile(JoinPoint joinPoint, Object response) {
        if (response instanceof ResponseEntity) {
            ResponseEntity res = (ResponseEntity) response;
            log.info("Advice Method : {}", joinPoint.getSignature().toString());
            log.info("Advice Response : {}", res.getBody());
        } else {
            log.info("Advice Method : {}", joinPoint.getSignature().toString());
            log.info("Advice Response : {}", response);
        }
    }

}
