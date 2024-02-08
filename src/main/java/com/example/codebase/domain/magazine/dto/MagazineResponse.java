package com.example.codebase.domain.magazine.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class MagazineResponse {

    @Getter
    @Setter
    @Schema(description = "매거진 생성 응답")
    public static class Get {
        private Long id;

        private String title;

        private String content;

        private String category;

        private Integer views;

        private Integer likes;

        private Integer comments;

        private AuthorResponse author;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        public static Get from(Magazine magazine) {
            Get response = new Get();
            response.id = magazine.getId();
            response.title = magazine.getTitle();
            response.content = magazine.getContent();
            response.category = magazine.getCategory().getName();
            response.views = magazine.getViews();
            response.likes = magazine.getLikes();
            response.comments = magazine.getComments();
            response.author = AuthorResponse.from(magazine.getMember());
            response.createdTime = magazine.getCreatedTime();
            response.updatedTime = magazine.getUpdatedTime();
            return response;
        }
    }

    @Getter
    @Setter
    public static class GetAll {
        private List<Get> magazines;

        private PageInfo pageInfo;

        public static GetAll from(Page<Magazine> magazines) {
            GetAll response = new GetAll();

            response.magazines = magazines.stream()
                    .map(Get::from)
                    .toList();
            response.pageInfo = PageInfo.from(magazines);
            return response;
        }
    }
}
