package com.example.codebase.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
public class LoginDTO {

    @Valid
    private String username;
    @Valid
    private String password;

}
