package com.example.codebase.domain.magazine.dto;

import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

public class MagazineRequest {

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Create", description = "매거진 생성 DTO")
    public static class Create {

        private String title;

        private String content;

        private Long categoryId;
    }

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Update", description = "매거진 수정 DTO")
    public static class Update {

        private String title;

        private String content;

    }
}
