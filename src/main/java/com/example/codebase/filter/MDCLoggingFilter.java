package com.example.codebase.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final UUID uuid = UUID.randomUUID();
        MDC.put("request_id", uuid.toString());
        chain.doFilter(request, response);
        // MDC 초기화 필요. (MDC는 ThreadLocal을 사용하기 때문에, Thread가 종료되면 자동으로 초기화되지 않음)
        MDC.clear();
    }
}
