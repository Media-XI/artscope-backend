package com.example.codebase.filter;

import io.sentry.Sentry;
import io.sentry.protocol.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class SentryInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ipAddress = getClientIpAddress(request);
        User realIpUser = new User();
        realIpUser.setIpAddress(ipAddress);
        if (ipAddress != null) {
            Sentry.configureScope(scope -> {
                scope.setUser(realIpUser);
            });
        }
        return true;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            return xForwardedForHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
