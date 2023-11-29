package com.example.codebase.domain.search.type;

import com.example.codebase.domain.member.entity.RoleStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum SearchSortType {
    정확도순("_score"),
    최신순("createdTime");

    String value;

    private SearchSortType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SearchSortType create(String searchSortType) {
        return Stream.of(SearchSortType.values())
                .filter(status -> status.name().equals(searchSortType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("부적절한 정렬 타입 입니다."));
    }


}
