package com.example.codebase.domain.member.entity;

import com.example.codebase.domain.artwork.entity.ArtworkMediaType;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum ArtistStatus {
    NONE, PENDING, APPROVED, REJECTED;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ArtistStatus create(String value) {
        return Stream.of(ArtistStatus.values())
                .filter(status -> status.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("부적절한 미디어 타입입니다. 지원하는 형식 : " +
                        Stream.of(ArtistStatus.values())
                                .map(ArtistStatus::name)
                                .reduce((a, b) -> a + ", " + b)
                                .get()));
    }

}
