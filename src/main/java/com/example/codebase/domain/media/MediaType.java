package com.example.codebase.domain.media;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum MediaType {
    image,
    video,
    audio,

    url;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MediaType create(String type) {
        return Stream.of(MediaType.values())
            .filter(mediaType -> mediaType.name().equals(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("부적절한 미디어 타입입니다. 지원하는 형식 : " +
                Stream.of(MediaType.values())
                    .map(MediaType::name)
                    .reduce((a, b) -> a + ", " + b)
                    .get()));
    }
}


