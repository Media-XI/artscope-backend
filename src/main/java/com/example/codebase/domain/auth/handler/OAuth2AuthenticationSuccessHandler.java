package com.example.codebase.domain.auth.handler;

import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private TokenProvider tokenProvider;
    private MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, MemberRepository memberRepository, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.createToken(authentication);
            TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
            tokenResponseDTO.setExpiresIn(tokenProvider.getTokenValidityInMilliseconds());
            tokenResponseDTO.setAccessToken(token);
            tokenResponseDTO.setToken_type("bearer");

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(mapper.writeValueAsString(tokenResponseDTO));

            log.info("token: " + token);
        } catch (Exception e) {
            throw e;
        }
    }

}
