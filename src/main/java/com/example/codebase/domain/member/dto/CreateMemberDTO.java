package com.example.codebase.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class CreateMemberDTO {


        @NotBlank(message = "아이디는 필수 입력입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 4~12자의 영문자와 숫자로만 입력 가능합니다.")
        private String username;

        @NotBlank(message = "비밀번호는 필수 입력입니다.")
        private String password;

        @NotBlank(message = "작가명은 필수 입력입니다.")
        private String name;

        @NotBlank(message = "이메일은 필수 입력입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
}
