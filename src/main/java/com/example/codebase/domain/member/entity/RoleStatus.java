package com.example.codebase.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum RoleStatus {
    NONE, ARTIST_PENDING, ARTIST_REJECTED, ARTIST, CURATOR_PENDING, CURATOR_REJECTED, CURATOR;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RoleStatus create(String value) {
        return Stream.of(RoleStatus.values())
                .filter(status -> status.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("부적절한 역활 상태 입니다."));
    }
}
