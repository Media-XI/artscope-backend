package com.example.codebase.domain.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsernameDTO {

    @NotBlank(message = "아이디는 필수 입력입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$", message = "아이디는 4~12자의 영문자와 숫자로만 입력 가능합니다.")
    private String username;


}
