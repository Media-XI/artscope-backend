package com.example.codebase.filter;

import com.example.codebase.filter.wrapper.RequestWrapper;
import com.example.codebase.filter.wrapper.ResponseWrapper;
import com.example.codebase.util.ClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class ServletLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain);
        }
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        try {
            logRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            logResponse(response);
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(RequestWrapper request) throws IOException {
        // log Request
        String remoteIP = ClientUtil.getRemoteIP(request);
        String queryString = request.getQueryString();
        log.info("Request [ remote-ip={} method={} uri={} content-type={} ]",
                remoteIP,
                request.getMethod(),
                queryString == null ? request.getRequestURI() : request.getRequestURI()  + "?" + queryString,
                request.getContentType()
        );

        if (!passUri(request.getRequestURI())) {
            logPayload("Request", request.getContentType(), request.getInputStream());
        }
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logPayload("Response", response.getContentType(), response.getContentInputStream());
    }

    private static void logPayload(String prefix, String contentType, InputStream inputStream) throws IOException {
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType)); // default: application/json

        // ContentType이 있다면 페이로드를 출력한다.
        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                String contentString = new String(content);
                if (contentString.length() > 1000) {
                    contentString = contentString.substring(0, 1000) + "...";
                }

                if (contentString.contains("<!-- HTML for static distribution bundle build -->")) {
                    log.info("{} Payload: HTML Content", prefix);
                    return ;
                }

                if (prefix.equals("Response"))
                    log.info("{} Payload: {} \n", prefix, contentString);
                else {
                    log.info("{} Payload: {} ", prefix, contentString);
                }
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
        );

        return VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
    }

    private static boolean passUri(String uri) {
        final List<String> PASS_TARGETS = Arrays.asList(
                "/api/login"
        );

        return PASS_TARGETS.stream()
                .anyMatch(passTarget -> uri.equals(passTarget));
    }

}
