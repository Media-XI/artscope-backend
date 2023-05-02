package com.example.codebase.filter;

import com.example.codebase.util.ClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ServletLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logRequest(request);
        filterChain.doFilter(request, response);
    }
    private static void logRequest(HttpServletRequest request) throws IOException {
        // log Request
        String remoteIP = ClientUtil.getRemoteIP(request);
        String queryString = request.getQueryString();
        log.info("Request info [ remote-ip={} method={} uri={} content-type={} ]",
                remoteIP,
                request.getMethod(),
                queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString,
                request.getContentType()
        );
    }

}