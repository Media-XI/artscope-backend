package com.example.codebase.domain.agora.dto;

import com.example.codebase.controller.dto.PageInfo;

import java.util.List;

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
