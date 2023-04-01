package com.example.codebase.exception;

public class NotAccessException extends RuntimeException {
    public NotAccessException() {
        super("접근 권한이 없습니다.");
    }

    public NotAccessException(String message) {
        super(message);
    }
}
