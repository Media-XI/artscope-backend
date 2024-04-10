package com.example.codebase.aop;


import com.example.codebase.exception.LoginRequiredException;
import com.example.codebase.util.RedisUtil;
import com.example.codebase.util.SecurityUtil;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("test")
public class CheckDuplicatedRequestAspectMock extends CheckDuplicatedRequestAspect {

    @Autowired
    public CheckDuplicatedRequestAspectMock(RedisUtil redisUtil) {
        super(redisUtil);
    }

    @Override
    public void checkDuplicateRequest(JoinPoint joinPoint) {
        String username = SecurityUtil.getCurrentUsername().orElseThrow(LoginRequiredException::new);
        if (!username.equals("testid")) {
            super.checkDuplicateRequest(joinPoint);
        }
    }
}