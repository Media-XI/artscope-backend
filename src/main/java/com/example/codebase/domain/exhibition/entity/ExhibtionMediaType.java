package com.example.codebase.domain.exhibition.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum ExhibtionMediaType {
    image,
    video;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExhibtionMediaType create(String type) {
        return Stream.of(ExhibtionMediaType.values())
                .filter(mediaType -> mediaType.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("부적절한 미디어 타입입니다. 지원하는 형식 : " +
                        Stream.of(ExhibtionMediaType.values())
                                .map(ExhibtionMediaType::name)
                                .reduce((a, b) -> a + ", " + b)
                                .get()));
    }

}
