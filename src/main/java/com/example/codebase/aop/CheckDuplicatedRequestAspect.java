package com.example.codebase.aop;

import com.example.codebase.annotation.CheckDuplicatedRequest;
import com.example.codebase.annotation.LoginOnly;
import com.example.codebase.annotation.UserAdminOnly;
import com.example.codebase.annotation.UserOnly;
import com.example.codebase.exception.DuplicatedRequestException;
import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.RedisUtil;
import com.example.codebase.util.SecurityUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Profile("!test")
public class CheckDuplicatedRequestAspect {

    private final RedisUtil redisUtil;

    @Autowired
    private CheckDuplicatedRequestAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Before("@annotation(com.example.codebase.annotation.CheckDuplicatedRequest)")
    public void checkDuplicateRequest(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CheckDuplicatedRequest annotation = method.getAnnotation(CheckDuplicatedRequest.class);

        if (!isValidAnnotation(method)) return;

        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);

        if (username.isEmpty()) return;

        String target = annotation.target().isEmpty() ? String.valueOf(method) : annotation.target();

        String uniqueKey = username + "->" + target;
        if (redisUtil.getData(uniqueKey).isPresent()) {
            throw new DuplicatedRequestException();
        }
        redisUtil.setDataAndExpire(uniqueKey, String.valueOf(LocalDateTime.now()), annotation.aliveMillisecondTime());
    }

    private boolean isValidAnnotation(Method method) {
        return method.isAnnotationPresent(LoginOnly.class)
                || method.isAnnotationPresent(UserOnly.class)
                || method.isAnnotationPresent(UserAdminOnly.class);
    }
}