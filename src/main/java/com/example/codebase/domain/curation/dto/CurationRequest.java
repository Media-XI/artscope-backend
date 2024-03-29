package com.example.codebase.domain.curation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CurationRequest {


    @Getter
    @Setter
    @Schema(name = "CurationRequest.Create", description = "큐레이션 생성 DTO")
    public static class Create{

        @NotNull(message = "매거진 아이디를 입력해주세요.")
        @Size(min = 1, max = 22, message = "매거진 아이디는 1개에서 22개까지 입력이 가능합니다.")
        private List<Long> magazineIds;

    }


    @Getter
    @Setter
    @Schema(name = "CurationRequest.Update", description = "큐레이션 수정 DTO")
    public static class Update{

        @NotNull(message = "큐레이션 아이디를 입력해주세요.")
        private Long curationId;

        @NotNull(message = "매거진 아이디를 입력해주세요.")
        private Long magazineId;
    }

}
