package com.example.codebase.domain.magazine.dto;

import com.example.codebase.domain.magazine.entity.MagazineComment;
import com.example.codebase.domain.post.dto.PostCommentResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MagazineCommentRepsonse {


    @Getter
    @Setter
    @Schema(name = "MagazineCommentResponse.Get", description = "매거진 댓글 생성 응답")
    public static class Get {

        private Long id;

        private String comment;

        private String mentionUsername;

        private Integer comments;

        private AuthorResponse author;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        private Long parentCommentId;

        private List<Get> childComments;

        public static Get from(MagazineComment magazineComment) {
            Get response = new Get();
            response.id = magazineComment.getId();
            response.comment = magazineComment.getComment();
            response.mentionUsername = magazineComment.getMentionUsername();
            response.comments = magazineComment.getComments();
            response.author = AuthorResponse.from(magazineComment.getMember());
            response.createdTime = magazineComment.getCreatedTime();
            response.updatedTime = magazineComment.getUpdatedTime();

            // 자식 댓글들 DTO로 추가
            response.parentCommentId = magazineComment.getParentCommentId();
            response.childComments = magazineComment.getChildComments().stream()
                    .map(MagazineCommentRepsonse.Get::from)
                    .toList();
            return response;
        }
    }
}
