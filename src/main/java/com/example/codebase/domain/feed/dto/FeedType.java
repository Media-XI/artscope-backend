package com.example.codebase.domain.feed.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum FeedType {
    artwork,

    post,

    event,

    agora;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FeedType create(String type) {
        return Stream.of(FeedType.values())
            .filter(feedType -> feedType.name().equals(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("부적절한 피드 타입입니다. 지원하는 형식 : " +
                Stream.of(FeedType.values())
                    .map(FeedType::name)
                    .reduce((a, b) -> a + ", " + b)
                    .get()));
    }

}
