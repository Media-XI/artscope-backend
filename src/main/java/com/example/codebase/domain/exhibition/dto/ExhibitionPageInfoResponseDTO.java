package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExhibitionPageInfoResponseDTO {

    List<ExhibitionResponseDTO> exhibitions = new ArrayList<>();

    PageInfo pageInfo = new PageInfo();

    public static ExhibitionPageInfoResponseDTO of(
        List<ExhibitionResponseDTO> dtos, PageInfo pageInfo) {
        ExhibitionPageInfoResponseDTO responseDTO = new ExhibitionPageInfoResponseDTO();
        responseDTO.exhibitions = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }

    public void addExhibition(ExhibitionResponseDTO from) {
        this.exhibitions.add(from);
    }
}
