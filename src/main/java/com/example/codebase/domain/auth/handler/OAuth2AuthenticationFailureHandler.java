package com.example.codebase.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.fail-redirect-uri}")
    private String failRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        String encodeMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        String redirectUri = failRedirectUri + "?error=" + encodeMessage;
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
}
