package com.example.codebase.domain.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import static lombok.AccessLevel.PROTECTED;

public class TeamRequest {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Schema(name = "TeamRequest.Create", description = "팀 생성 DTO")
    public static class Create {

        @NotEmpty(message = "팀 이름은 필수입니다.")
        @Size(max = 50, message = "팀 이름은 50자 이하여야 합니다.")
        private String name;

        @Size(max = 255, message = "팀 주소는 최대 255자 까지 입력 가능합니다.")
        private String address;

        @NotEmpty(message = "팀 프로필 이미지는 필수 입니다")
        @URL(message = "프로필 이미지 URL이 유효하지 않습니다.")
        private String profileImage;

        @NotEmpty(message = "팀 배경화면 이미지는 필수 입니다")
        @URL(message = "배경화면 이미지 URL이 유효하지 않습니다.")
        private String backgroundImage;

        @NotEmpty(message = "팀 소개는 필수입니다.")
        private String description;

        @NotEmpty(message = "본인의 포지션은 필수입니다.")
        @Size(max = 100, message = "포지션은 100자 이하여야 합니다.")
        private String position;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    @Schema(name = "TeamRequest.Update", description = "팀 수정 DTO")
    public static class Update {

        @NotEmpty(message = "팀 이름은 필수입니다.")
        @Size(max = 50, message = "팀 이름은 50자 이하여야 합니다.")
        private String name;

        @Size(max = 255, message = "팀 주소는 최대 255자 까지 입력 가능합니다.")
        private String address;

        @NotEmpty(message = "팀 프로필 이미지는 필수 입니다")
        @URL(message = "프로필 이미지 URL이 유효하지 않습니다.")
        private String profileImage;

        @NotEmpty(message = "팀 배경화면 이미지는 필수 입니다")
        @URL(message = "배경화면 이미지 URL이 유효하지 않습니다.")
        private String backgroundImage;

        @NotEmpty(message = "팀 소개는 필수입니다.")
        private String description;
    }
}
