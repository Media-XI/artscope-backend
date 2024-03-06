package com.example.codebase.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Aspect
@Component
public class CustomAspectLogging {

    @Before("within(com.example.codebase.controller.advice.CustomExceptionHandler)")
    public void logBeforeException(JoinPoint joinPoint) {
        Exception e = (Exception) joinPoint.getArgs()[0];
        log.error(printStackTrage(e));
    }

    @AfterReturning(value = "within(com.example.codebase.controller.advice.CustomExceptionHandler)", returning = "response")
    public void logResponseException(JoinPoint joinPoint, Object response) {
        if (response instanceof ResponseEntity) {
            ResponseEntity res = (ResponseEntity) response;
            log.info("Advice Method : {}", joinPoint.getSignature().toString());
            log.info("Advice Response : {}", res.getBody());
        } else {
            log.info("Advice Method : {}", joinPoint.getSignature().toString());
            log.info("Advice Response : {}", response);
        }
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

    private String printStackTrage(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


}
