package com.example.codebase.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ExecutionTimeLogging {

    @Around(value = "bean(*Controller)", argNames = "joinPoint")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("Controller ExecutionTime : {}ms - [{}]", executionTime, joinPoint.getSignature());
        return proceed;
    }

    @AfterReturning(value = "within(com.example.codebase.controller.advice.*)", returning = "response")
    public void logResponse(JoinPoint joinPoint, Object response) {
        log.info("Advice Method : {}", joinPoint.getSignature().toString());
        // log.info("Advice Error : {}", joinPoint.getTarget());
        log.info("Advice Response : {}", response);
    }
}
