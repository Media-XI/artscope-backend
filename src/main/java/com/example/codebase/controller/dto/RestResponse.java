package com.example.codebase.controller.dto;

import lombok.Getter;

@Getter
public class RestResponse {

    private boolean success;

    private String message;

    private String detail;

    private String code;

    public RestResponse(boolean success, String message, String code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }

    public RestResponse(boolean success, String message, Integer code) {
        this.success = success;
        this.message = message;
        this.code = String.valueOf(code);
    }

    public RestResponse(boolean success, String message, String detail, String code) {
        this.success = success;
        this.message = message;
        this.detail = detail;
        this.code = code;
    }
}
