package com.example.codebase.domain.member.exception;

public class ExistMemberException extends RuntimeException{
    public ExistMemberException(String message) {
        super(message);
    }

    public ExistMemberException() {
        super("이미 존재하는 회원입니다.");
    }
}
