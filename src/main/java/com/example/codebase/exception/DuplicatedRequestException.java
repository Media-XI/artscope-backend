package com.example.codebase.exception;

public class DuplicatedRequestException extends RuntimeException {

    public DuplicatedRequestException() {
        super("잠시 후 다시 시도해주세요.");
    }

    public DuplicatedRequestException(String message) {
        super(message);
    }
}
