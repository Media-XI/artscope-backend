package com.example.codebase.domain.magazine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

public class MagazineRequest {

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Create", description = "매거진 생성 DTO")
    public static class Create {

        @NotEmpty
        private String title;

        @NotEmpty
        private String content;

        @NotNull
        private Long categoryId;
    }

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Update", description = "매거진 수정 DTO")
    public static class Update {

        @NotEmpty
        private String title;

        @NotEmpty
        private String content;

    }
}
