package com.example.codebase.auth.oauth;

import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @Autowired
    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            String token = tokenProvider.createToken(authentication);
            response.addHeader("Authorization", "Bearer " + token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            log.info("token: " + token);
        } catch (Exception e) {
            throw e;
        }
    }

}
