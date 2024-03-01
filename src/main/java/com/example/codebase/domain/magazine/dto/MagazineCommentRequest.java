package com.example.codebase.domain.magazine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

public class MagazineCommentRequest {

    @Getter
    @Setter
    @Schema(name = "MagazineCommentRequest.Create", description = "매거진 댓글 생성 DTO")
    public static class Create {

        @NotEmpty(message = "댓글 내용을 작성해주세요.")
        private String comment;

        private Long parentCommentId;
    }

    @Getter
    @Setter
    @Schema(name = "MagazineCommentRequest.Update", description = "매거진 댓글 수정 DTO")
    public static class Update {
        @NotEmpty(message = "댓글 내용을 작성해주세요.")
        private String comment;
    }
}
