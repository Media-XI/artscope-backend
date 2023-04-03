package com.example.codebase.exception;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() {
        super();
    }

    public InvalidJwtTokenException(String message) {
        super(message);
    }
}