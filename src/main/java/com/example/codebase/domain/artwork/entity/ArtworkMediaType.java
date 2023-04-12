package com.example.codebase.domain.artwork.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum ArtworkMediaType {
    image,
    video,
    audio;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ArtworkMediaType create(String type) {
        return Stream.of(ArtworkMediaType.values())
                .filter(mediaType -> mediaType.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("부적절한 미디어 타입입니다. 지원하는 형식 : " +
                        Stream.of(ArtworkMediaType.values())
                                .map(ArtworkMediaType::name)
                                .reduce((a, b) -> a + ", " + b)
                                .get()));
    }
}
