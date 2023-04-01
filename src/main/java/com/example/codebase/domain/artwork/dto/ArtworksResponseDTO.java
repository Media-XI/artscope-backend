package com.example.codebase.domain.artwork.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;


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
