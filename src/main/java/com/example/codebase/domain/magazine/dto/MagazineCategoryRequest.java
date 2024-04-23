package com.example.codebase.domain.magazine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

public class MagazineCategoryRequest {

    @Getter
    @Setter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    @Schema(name = "MagazineCategoryRequest", description = "카테고리 생성 요청")
    public static class Create {

        @Schema(description = "카테고리 이름", example = "Post")
        @NotNull(message = "카테고리 이름은 필수입니다.")
        private String name;

        @Schema(description = "카테고리 슬러그(영어)", example = "post-category")
        @NotNull(message = "슬러그는 필수입니다.")
        @Pattern(regexp = "^[a-zA-Z]+(-[a-zA-Z]+)?$", message = "슬러그는 영어, - 로만 작성 가능합니다.")
        private String slug;

        @Schema(description = "부모 카테고리 ID", example = "1")
        private Long parentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor
    @Schema(name = "MagazineCategoryRequest.Update", description = "카테고리 수정 요청")
    public static class Update {

        @Schema(description = "카테고리 이름", example = "Post")
        private String name;

        @Schema(description = "카테고리 슬러그(영어)", example = "post-category")
        @Pattern(regexp = "^[a-zA-Z]+(-[a-zA-Z]+)?$", message = "슬러그는 영어, - 로만 작성 가능합니다.")
        private String slug;

        @Schema(description = "부모 카테고리 ID", example = "1")
        private Long parentId;
    }
}
