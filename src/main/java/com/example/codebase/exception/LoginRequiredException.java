package com.example.codebase.exception;

public class LoginRequiredException extends RuntimeException {

    public LoginRequiredException(String message) {
        super(message);
    }

    public LoginRequiredException() {
        super("로그인이 필요합니다.");
    }
}
