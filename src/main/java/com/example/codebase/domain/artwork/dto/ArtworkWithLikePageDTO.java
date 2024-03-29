package com.example.codebase.domain.artwork.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ArtworkWithLikePageDTO {
    List<ArtworkWithIsLikeResponseDTO> artworks;

    PageInfo pageInfo;

    public static ArtworkWithLikePageDTO of(List<ArtworkWithIsLikeResponseDTO> dtos, PageInfo pageInfo) {
        ArtworkWithLikePageDTO artworkWithLikePageDTO = new ArtworkWithLikePageDTO();
        artworkWithLikePageDTO.setArtworks(dtos);
        artworkWithLikePageDTO.setPageInfo(pageInfo);
        return artworkWithLikePageDTO;
    }

}
