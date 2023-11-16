package com.example.codebase.filter.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
public class RefererCheckIntercepter implements HandlerInterceptor {

    private final Environment environment;

    public RefererCheckIntercepter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String referer = request.getHeader("Referer");
        String host = request.getHeader("Host");

        // 운영 환경이 아니면 referer 체크하지 않음
        if (!Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return true;
        }

        // referer가 없거나, host와 다르면 차단
        if (referer == null || !referer.contains(host) || !referer.contains("artscope.kr")) {
            log.info("유효하지 않은 Referer 요청 referer={}, host={}", referer, host);
            return false;
        }

        return true;
    }
}
