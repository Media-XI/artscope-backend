package com.example.codebase.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMemberDTO {

        private String username;
        private String password;
        private String name;
        private String email;
}
