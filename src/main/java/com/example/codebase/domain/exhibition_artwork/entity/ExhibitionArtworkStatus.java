package com.example.codebase.domain.exhibition_artwork.entity;

import com.example.codebase.domain.artwork.entity.MediaType;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum ExhibitionArtworkStatus {
    submitted,
    pending,
    accepted,
    rejected;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ExhibitionArtworkStatus create(String type) {
        return Stream.of(ExhibitionArtworkStatus.values())
                .filter(status -> status.name().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("부적절한 상태입니다. 지원하는 형식 : " +
                        Stream.of(ExhibitionArtworkStatus.values())
                                .map(ExhibitionArtworkStatus::name)
                                .reduce((a, b) -> a + ", " + b)
                                .get()));
    }

}
