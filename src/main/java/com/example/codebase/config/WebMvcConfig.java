package com.example.codebase.config;

import com.example.codebase.filter.MDCLoggingFilter;
import com.example.codebase.filter.ServletLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public MDCLoggingFilter mdcLoggingFilter() {
        return new MDCLoggingFilter();
    }

    @Bean
    public ServletLoggingFilter servletLoggingFilter() {
        return new ServletLoggingFilter();
    }
}
