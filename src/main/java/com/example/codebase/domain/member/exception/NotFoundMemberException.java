package com.example.codebase.domain.member.exception;

public class NotFoundMemberException extends RuntimeException{
    public NotFoundMemberException() {
        super("해당 회원을 찾을 수 없습니다.");
    }
}
