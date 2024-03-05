package com.example.codebase.domain.curation.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.magazine.dto.AuthorResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class CurationResponse {

    @Getter
    @Setter
    @Schema(name = "CurationResponse.Get", description = "큐레이션 생성 응답 DTO")
    public static class Get {

        private Long curationId;

        private Long magazineId;

        private String magazinetitle;

        private String magazinecontent;

        private String category;

        private Integer views;

        private Integer likes;

        private Integer comments;

        private AuthorResponse author;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime curationUpdatedTime;

        public static CurationResponse.Get from(Curation curation) {
            Get get = new Get();
            get.setCurationId((curation.getId()));
            get.setMagazineId(curation.getMagazine().getId());
            get.setMagazinetitle(curation.getMagazine().getTitle());
            get.setMagazinecontent(curation.getMagazine().getContent());
            get.setCategory(curation.getMagazine().getCategory().getName());
            get.setViews(curation.getMagazine().getViews());
            get.setLikes(curation.getMagazine().getLikes());
            get.setComments(curation.getMagazine().getComments());
            get.setAuthor(AuthorResponse.from(curation.getMagazine().getMember()));
            get.setCreatedTime(curation.getMagazine().getCreatedTime());
            get.setUpdatedTime(curation.getMagazine().getUpdatedTime());
            get.setCurationUpdatedTime(curation.getUpdatedTime());
            return get;
        }
    }

    @Getter
    @Setter
    @Schema(name = "CurationResponse.GetALL", description = "큐레이션 목록 조회 응답 DTO")
    public static class GetAll {
        private List<Get> curations;

        private PageInfo pageInfo;

        public static CurationResponse.GetAll from(Page<Curation> curations) {
            GetAll response = new GetAll();

            response.curations = curations.stream()
                    .map(Get::from)
                    .toList();
            response.pageInfo = PageInfo.from(curations);

            return response;
        }
    }
}
