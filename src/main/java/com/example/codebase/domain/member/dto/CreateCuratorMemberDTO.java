package com.example.codebase.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateCuratorMemberDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    // @Patter URL 검증하는 어노테이션 추가
    @Size(min = 1, max = 120, message = "주소는 1~120자 이내로 입력해주세요.")
    @Pattern(regexp = "^(http|https)://.*", message = "SNS 주소를 입력해주세요.")
    private String snsUrl;

    @Size(min = 1, max = 120, message = "주소는 1~120자 이내로 입력해주세요.")
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요.")
    private String websiteUrl;

    @Size(min = 1, max = 1000, message = "소개는 1~1000자 이내로 입력해주세요.")
    @NotBlank(message = "소개를 입력해주세요.")
    private String introduction;

    @Size(min = 1, max = 1000, message = "히스토리는 1~1000자 이내로 입력해주세요.")
    @NotBlank(message = "히스토리를 입력해주세요.")
    private String history;

    @Size(min = 1, max = 100, message = "회사명은 1~100자 이내로 입력해주세요.")
    @NotBlank(message = "회사명을 입력해주세요.")
    private String companyName;

    @Size(min = 1, max = 50, message = "직무는 1~50자 이내로 입력해주세요.")
    @NotBlank(message = "직무를 입력해주세요.")
    private String companyRole;
}
