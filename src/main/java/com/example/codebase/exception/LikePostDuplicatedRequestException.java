package com.example.codebase.exception;

public class LikePostDuplicatedRequestException extends RuntimeException {

    public LikePostDuplicatedRequestException() {
        super("좋아요 요청을 자주 보내셨습니다. 잠시 후 다시 시도해주세요.");
    }
}
