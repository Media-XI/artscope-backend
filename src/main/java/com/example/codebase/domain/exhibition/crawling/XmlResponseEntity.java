package com.example.codebase.domain.exhibition.crawling;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class XmlResponseEntity extends ResponseEntity<String> {

    public XmlResponseEntity(String body, HttpStatusCode status) {
        super(body, status);
    }

    public void statusCodeCheck() {
        if (!getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("API 호출 에러");
        }
    }
}

