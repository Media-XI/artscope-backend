package com.example.codebase.domain.magazine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class MagazineCategoryRequest {

    @Getter
    @Setter
    @Schema(name = "MagazineCategoryRequest", description = "카테고리 생성 요청")
    public static class Create {

        @Schema(description = "카테고리 이름", example = "Post")
        private String name;
    }

}
