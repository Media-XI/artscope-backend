package com.example.codebase.filter;

import com.example.codebase.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    public JwtFilter(
        TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            if (!tokenProvider.isRefreshType(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Security Context에 '{}' 인증정보를 저장했습니다, uri : {}", authentication.getName(), requestURI);
            }
        } else {
            log.info("유효한 JWT 토큰이 없습니다, uri : {}", requestURI);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }

            Cookie acceeTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("access-token"))
                .findFirst()
                .orElse(null);

            if (acceeTokenCookie == null) {
                return null;
            }
            bearerToken = acceeTokenCookie.getValue();
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }

        return null;
    }
}
