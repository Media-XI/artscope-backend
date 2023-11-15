package com.example.codebase.config;

import com.example.codebase.domain.auth.handler.OAuth2AuthenticationFailureHandler;
import com.example.codebase.domain.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.example.codebase.domain.auth.service.CustomOAuth2UserService;
import com.example.codebase.jwt.JwtAccessDeniedHandler;
import com.example.codebase.jwt.JwtAuthenticationEntryPoint;
import com.example.codebase.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final String[] permitList = {
            "/v2/**",
            "/v3/**",
            "/configuration/**",
            "/swagger*/**",
            "/webjars/**",
            "/swagger-resources/**"
    };

    @Autowired
    public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler,
                          CustomOAuth2UserService customOAuth2UserService) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.customOAuth2UserService = customOAuth2UserService;
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers("/h2-console/**", "/favicon.ico")
//                .antMatchers(permitList);
//    }

/*
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
           return (web) -> web.ignoring().antMatchers("/ignore1", "/ignore2");
        }
 */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .headers().frameOptions().disable()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .securityMatcher("/api/**")
                .securityMatcher("/hizz")
                .securityMatcher(permitList)
                .authorizeHttpRequests((authz) ->
                        authz
                                .requestMatchers("/api/**", "/hizz").permitAll()
                                .requestMatchers(permitList).hasAnyAuthority("ROLE_ADMIN")
                                .anyRequest().authenticated()
                )
                .apply(new JwtSecurityConfig(tokenProvider))

                .and()
                .oauth2Login()  // oidc
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .userInfoEndpoint().userService(customOAuth2UserService);
        return http.build();
    }
}

