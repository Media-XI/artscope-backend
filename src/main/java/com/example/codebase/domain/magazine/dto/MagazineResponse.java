package com.example.codebase.domain.magazine.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.magazine.entity.MagazineMedia;
import com.example.codebase.domain.magazine.entity.MagazineWithIsLiked;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MagazineResponse {

    @Getter
    @Setter
    @Schema(description = "매거진 조회 응답")
    public static class Get {
        private Long id;

        private String title;

        private String content;

        private Map<String, String> metadata;

        private Long categoryId;

        private String categorySlug;

        private String categoryName;

        private List<String> mediaUrls;

        private Integer views;

        private Integer likes;

        private Integer comments;

        private AuthorResponse author;

        private String teamName;

        private Long teamId;
      
        private Boolean isLiked = false;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        private List<MagazineCommentRepsonse.Get> magazineComments = Collections.emptyList();

        public static Get from(Magazine magazine) {
            Get response = new Get();
            response.id = magazine.getId();
            response.title = magazine.getTitle();
            response.content = magazine.getContent();
            response.metadata = magazine.getMetadata();
            response.mediaUrls = magazine.getMagazineMedias().stream()
                    .map(MagazineMedia::getUrl)
                    .toList();
            response.categoryId = magazine.getCategory().getId();
            response.categorySlug = magazine.getCategory().getSlug();
            response.categoryName = magazine.getCategory().getName();
            response.views = magazine.getViews();
            response.likes = magazine.getLikes();
            response.comments = magazine.getComments();
            response.author = AuthorResponse.from(magazine.getMember());
            response.createdTime = magazine.getCreatedTime();
            response.updatedTime = magazine.getUpdatedTime();

            if (magazine.getTeam() != null) {
                response.setTeamId(magazine.getTeam().getId());
                response.setTeamName(magazine.getTeam().getName());
            }


            // 1차 댓글만 가져오기
            response.magazineComments = magazine.getMagazineComments().stream()
                    .filter((comment) -> comment.getParentComment() == null)
                    .filter((comment) -> comment.getChildComments() != null)
                    .map(MagazineCommentRepsonse.Get::from) // 1차 댓글을 DTO로 변환 (이때 자식 댓글도 변환)
                    .toList();

            return response;
        }

        public static Get from(MagazineWithIsLiked magazineWithIsLiked) {
            Get response = from(magazineWithIsLiked.getMagazine());
            response.isLiked = magazineWithIsLiked.getIsLiked();
            return response;
        }

        @JsonIgnore
        public Long getFirstCommentId() {
            if (magazineComments.isEmpty()) {
                return null;
            }
            return magazineComments.get(0).getId();
        }

        @JsonIgnore
        public Long getFirstChildCommentOfFirstComment() {
            if (magazineComments.isEmpty()) {
                return null;
            }
            if (magazineComments.get(0).getChildComments().isEmpty()) {
                return null;
            }
            return magazineComments.get(0).getChildComments().get(0).getId();
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

        public static GetAll withLike(Page<MagazineWithIsLiked> magazines) {
            GetAll response = new GetAll();

            response.magazines = magazines.stream()
                    .map(Get::from)
                    .toList();
            response.pageInfo = PageInfo.from(magazines);
            return response;
        }

    }
}
