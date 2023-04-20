package com.example.codebase.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;
import javax.validation.constraints.*;
import java.util.Optional;

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
    @Size(min = 1, max = 120, message = "주소는 1~120자 이내로 입력해주세요.")
    @Pattern(regexp = "^(http|https)://.*", message = "SNS 주소를 입력해주세요.")
    private String snsUrl;

    @Parameter(required = false)
    @Size(min = 1, max = 120, message = "주소는 1~120자 이내로 입력해주세요.")
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요.")
    private String websiteUrl;

    @Parameter(required = false)
    @Size(min = 1, max = 120, message = "소개는 1~120자 이내로 입력해주세요.")
    private String introduction;

    @Parameter(required = false)
    @Size(min = 1, max = 120, message = "히스토리는 1~120자 이내로 입력해주세요.")
    private String history;
}
