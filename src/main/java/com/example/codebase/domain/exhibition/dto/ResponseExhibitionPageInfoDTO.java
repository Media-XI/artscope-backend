package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.controller.dto.PageInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseExhibitionPageInfoDTO {

    List<ResponseExhibitionDTO> exhibitions;

    PageInfo pageInfo;

    public static ResponseExhibitionPageInfoDTO of(List<ResponseExhibitionDTO> dtos, PageInfo pageInfo) {
        ResponseExhibitionPageInfoDTO responseDTO = new ResponseExhibitionPageInfoDTO();
        responseDTO.exhibitions = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }

}
