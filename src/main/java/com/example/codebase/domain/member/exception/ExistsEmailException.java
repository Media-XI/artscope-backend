package com.example.codebase.domain.member.exception;

public class ExistsEmailException extends RuntimeException {

    public ExistsEmailException() {
        super("이미 가입한 이메일입니다.");
    }
}
