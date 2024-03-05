package com.example.codebase.domain.curation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class CurationRequest {


    @Getter
    @Setter
    @Schema(name = "CurationRequest.Create", description = "큐레이션 생성 DTO")
    public static class Create{
        @NotNull(message = "매거진 아이디를 입력해주세요.")
        private Long magazineId;
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
