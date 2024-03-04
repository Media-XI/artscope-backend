package com.example.codebase.domain.magazine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        // TODO: metadata 에 대해 검증 필요 (보안)
        @Schema(description = "JSON 형식의 메타데이터")
        private Map<String, String> metadata;

        @Size(max = 10, message = "최대 10개 까지 미디어 첨부 가능합니다.")
        private List<@URL(message = "올바른 URL 형식이 아닙니다.") String> mediaUrls = Collections.emptyList();
    }

    @Getter
    @Setter
    @Schema(name = "MagazineRequest.Update", description = "매거진 수정 DTO")
    public static class Update {

        @NotEmpty
        private String title;

        @NotEmpty
        private String content;

        private Map<String, String> metadata;

        private List<String> mediaUrls;
    }
}
