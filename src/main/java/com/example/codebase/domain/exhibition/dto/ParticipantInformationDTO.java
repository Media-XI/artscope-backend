package com.example.codebase.domain.exhibition.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantInformationDTO {

    private String username;

    private String name;

    public void checkParticipantValidity() {
        if(this.username != null && this.name != null) {
            throw new RuntimeException("참가자 정보는 username과 name 둘 중 하나만 존재해야 합니다.");
        }
    }
}


