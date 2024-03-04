package com.example.codebase.domain.curation.dto;

import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.magazine.dto.AuthorResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CurationResponse {

    @Getter
    @Setter
    @Schema(name = "CurationResponse.Get", description = "큐레이션 생성 응답 DTO")
    public static class Get{

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

        public static CurationResponse.Get from(Curation curation){
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
            return get;
        }
    }

    @Getter
    @Setter
    @Schema(name = "Many CurationResponse", description = "Many Curation Response")
    public static class GetAll{
        private List<Get> curationList;

        public static CurationResponse.GetAll from(List<Curation> curationList){
            GetAll getAll = new GetAll();
            List<CurationResponse.Get> curationResponseList = curationList.stream().map(CurationResponse.Get::from).collect(Collectors.toList());
            getAll.setCurationList(curationResponseList);
            return getAll;
        }
    }
}
