package com.example.codebase.controller.advice;

import com.example.codebase.util.ClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class CustomResponseBodyAdviceAdapter implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            log.info(ClientUtil.jsonBodyForLogging(body).insert(0, "Response").toString());
        } catch (IOException e) {
            log.info("Error while logging request body: " + e.getMessage());
        }
        return body;
    }
}
