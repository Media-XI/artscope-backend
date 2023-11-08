package com.example.codebase.domain.agora.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AgorasResponseDTO {

    List<AgoraReponseDTO> agoras;

    PageInfo pageInfo;

    public static AgorasResponseDTO of(List<AgoraReponseDTO> agoras, PageInfo pageInfo) {
        AgorasResponseDTO dto = new AgorasResponseDTO();
        dto.agoras = agoras;
        dto.pageInfo = pageInfo;
        return dto;
    }
}
