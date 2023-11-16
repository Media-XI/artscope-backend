package com.example.codebase.config;

import com.example.codebase.filter.MDCLoggingFilter;
import com.example.codebase.filter.ServletLoggingFilter;
import com.example.codebase.filter.security.RefererCheckIntercepter;
import com.example.codebase.util.HTMLCharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Environment environment; // 운영 환경 정보 가져오기 위한 Environment

    @Autowired
    public WebMvcConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public MDCLoggingFilter mdcLoggingFilter() {
        return new MDCLoggingFilter();
    }

    @Bean
    public ServletLoggingFilter servletLoggingFilter() {
        return new ServletLoggingFilter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(escapingConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefererCheckIntercepter(environment))
            .addPathPatterns("/api/**");
    }

    @Bean
    public HttpMessageConverter escapingConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 직/역직렬화를 위한 모듈 등록

        MappingJackson2HttpMessageConverter escapingConverter = new MappingJackson2HttpMessageConverter();
        escapingConverter.setObjectMapper(objectMapper);

        return escapingConverter;
    }
}
