package com.example.codebase.domain.member.exception;

import com.example.codebase.exception.NotFoundException;

public class NotFoundMemberException extends NotFoundException {
    public NotFoundMemberException() {
        super("해당 회원을 찾을 수 없습니다.");
    }
}
