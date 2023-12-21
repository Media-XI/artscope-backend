package com.example.codebase.domain.Event.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum EventMediaType {
    image,

    video;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EventMediaType create(String type) {
        return Stream.of(EventMediaType.values())
            .filter(mediaType -> mediaType.name().equals(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("부적절한 미디어 타입입니다. 지원하는 형식 : " +
                Stream.of(EventMediaType.values())
                    .map(EventMediaType::name)
                    .reduce((a, b) -> a + ", " + b)
                    .get()));
    }
}
