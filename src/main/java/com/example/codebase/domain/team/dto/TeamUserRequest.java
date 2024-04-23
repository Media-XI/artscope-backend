package com.example.codebase.domain.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

public class TeamUserRequest {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Schema(name = "TeamUserRequest.Create", description = "팀 유저 추가 DTO")
    public static class Create {

        @NotEmpty(message = "포지션은 필수입니다.")
        @Size(max = 100 , message = "포지션은 100자 이하여야 합니다.")
        private String position;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Schema(name = "TeamUserRequest.Update", description = "팀 유저 수정 DTO")
    public static class Update {

        @NotEmpty(message = "포지션은 필수입니다.")
        @Size(max = 100 , message = "포지션은 100자 이하여야 합니다.")
        private String position;
    }
}
