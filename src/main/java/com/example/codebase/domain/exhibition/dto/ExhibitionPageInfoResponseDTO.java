package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.controller.dto.PageInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExhibitionPageInfoResponseDTO {

    List<ExhibitionResponseDTO> exhibitions;

    PageInfo pageInfo;

    public static ExhibitionPageInfoResponseDTO of(
            List<ExhibitionResponseDTO> dtos, PageInfo pageInfo) {
        ExhibitionPageInfoResponseDTO responseDTO = new ExhibitionPageInfoResponseDTO();
        responseDTO.exhibitions = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }
}
