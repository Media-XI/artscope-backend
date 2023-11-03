package com.example.codebase.domain.artwork.dto;

import com.example.codebase.controller.dto.PageInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArtworkLikeMemberPageDTO {

    private List<ArtworkLikeMemberResponseDTO> dtos;

    private PageInfo pageInfo;

    public static ArtworkLikeMemberPageDTO of(List<ArtworkLikeMemberResponseDTO> dtos, PageInfo pageInfo) {
        ArtworkLikeMemberPageDTO artworkLikeMemberPageDTO = new ArtworkLikeMemberPageDTO();
        artworkLikeMemberPageDTO.setDtos(dtos);
        artworkLikeMemberPageDTO.setPageInfo(pageInfo);
        return artworkLikeMemberPageDTO;
    }
}
