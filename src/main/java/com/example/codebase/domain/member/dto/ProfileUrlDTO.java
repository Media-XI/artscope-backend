package com.example.codebase.domain.member.dto;

import jakarta.validation.constraints.Pattern;

public record ProfileUrlDTO(
        @Pattern(regexp = "^(https)://.*", message = "프로필 주소를 입력해주세요. (HTTPS만 허용)")
        String profile) {
}

