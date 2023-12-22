package com.example.codebase.domain.member.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class UpdateMemberDTO {

    @Parameter(required = false)
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 4~12자의 영문자와 숫자로만 입력 가능합니다.")
    private String username;

    @Parameter(required = false)
    private String name;

    @Parameter(required = false)
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Parameter(required = false)
    @Size(min = 1, max = 120, message = "SNS 주소는 1~120자 이내로 입력해주세요.")
    @Pattern(regexp = "^(http|https)://.*", message = "SNS 주소를 입력해주세요.")
    private String snsUrl;

    @Parameter(required = false)
    @Size(min = 1, max = 120, message = "사이트 주소는 1~120자 이내로 입력해주세요.")
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요.")
    private String websiteUrl;

    @Parameter(required = false)
    @Size(min = 1, max = 1000, message = "소개는 1~1000자 이내로 입력해주세요.")
    private String introduction;

    @Parameter(required = false)
    @Size(min = 1, max = 1000, message = "연혁은 1~1000자 이내로 입력해주세요.")
    private String history;
}
