package com.example.codebase.aop;

import com.example.codebase.annotation.CheckDuplicatedRequest;
import com.example.codebase.exception.DuplicatedRequestException;
import com.example.codebase.util.RedisUtil;
import com.example.codebase.util.SecurityUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class CheckDuplicatedRequestAspect {

    private final RedisUtil redisUtil;

    @Autowired
    private CheckDuplicatedRequestAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Before("@annotation(com.example.codebase.annotation.CheckDuplicatedRequest)")
    public void checkDuplicateRequest(JoinPoint joinPoint) {
        CheckDuplicatedRequest checkDuplicatedRequest = getAnnotation(joinPoint);
        String username = String.valueOf(SecurityUtil.getCurrentUsername());

        if(username.isEmpty()){
            return;
        }

        String target = checkDuplicatedRequest.target();

        String uniqueKey = username + "->" + target;
        if (redisUtil.getData(uniqueKey).isPresent()) {
            throw new DuplicatedRequestException();
        }
        redisUtil.setDataAndExpire(uniqueKey, String.valueOf(LocalDateTime.now()), 3000);
    }

    private CheckDuplicatedRequest getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(CheckDuplicatedRequest.class);
    }
}
