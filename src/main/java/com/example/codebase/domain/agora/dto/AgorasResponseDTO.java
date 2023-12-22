package com.example.codebase.domain.agora.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AgorasResponseDTO {

    List<AgoraResponseDTO> agoras = new ArrayList<>();

    PageInfo pageInfo = new PageInfo();

    public static AgorasResponseDTO of(List<AgoraResponseDTO> agoras, PageInfo pageInfo) {
        AgorasResponseDTO dto = new AgorasResponseDTO();
        dto.agoras = agoras;
        dto.pageInfo = pageInfo;
        return dto;
    }

    public void addAgora(AgoraResponseDTO from) {
        this.agoras.add(from);
    }
}
