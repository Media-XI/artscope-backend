package com.example.codebase.domain.exhibition_artwork.exception;

import com.example.codebase.exception.NotFoundException;

public class NotFoundExhibitionException extends NotFoundException {
    public NotFoundExhibitionException() {
        super("존재하지 않는 공모전입니다.");
    }
}
