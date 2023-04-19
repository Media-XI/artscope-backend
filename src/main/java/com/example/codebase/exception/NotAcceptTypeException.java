package com.example.codebase.exception;

public class NotAcceptTypeException extends RuntimeException {
    public NotAcceptTypeException(String message) {
        super(message);
    }

    public NotAcceptTypeException() {
        super("지원하지 않는 미디어 타입입니다.");
    }
}
