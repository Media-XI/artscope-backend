package com.example.codebase.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class LoginDTO {

    @NotBlank(message = "아이디는 필수 입력입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 4~12자의 영문자와 숫자로만 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    private String password;

}
