package com.example.codebase.domain.curation.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.magazine.dto.MagazineResponse;
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

        private MagazineResponse.Get magazine;

        private Long curationId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime curationUpdatedTime;

        public static CurationResponse.Get from(Curation curation) {
            Get get = new Get();
            get.magazine = MagazineResponse.Get.from(curation.getMagazine());
            get.curationId = curation.getId();
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

        public static CurationResponse.GetAll from(List<Curation> curations) {
            GetAll response = new GetAll();

            response.curations = curations.stream()
                    .map(Get::from)
                    .toList();

            response.pageInfo = null;
            return response;
        }
    }


}

