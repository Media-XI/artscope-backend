package com.example.codebase.domain.artwork.dto;

import com.example.codebase.controller.dto.PageInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ArtworksResponseDTO {
    List<ArtworkResponseDTO> artworks;

    PageInfo pageInfo;

    public static ArtworksResponseDTO of(List<ArtworkResponseDTO> dtos, PageInfo pageInfo) {
        ArtworksResponseDTO artworksResponseDTO = new ArtworksResponseDTO();
        artworksResponseDTO.setArtworks(dtos);
        artworksResponseDTO.setPageInfo(pageInfo);
        return artworksResponseDTO;
    }
}
