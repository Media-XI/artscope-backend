package com.example.codebase.domain.artwork.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
public class ArtworksResponseDTO {

    List<ArtworkResponseDTO> artworks = new ArrayList<>();

    PageInfo pageInfo = new PageInfo();

    public static ArtworksResponseDTO of(List<ArtworkResponseDTO> dtos, PageInfo pageInfo) {
        ArtworksResponseDTO artworksResponseDTO = new ArtworksResponseDTO();
        artworksResponseDTO.setArtworks(dtos);
        artworksResponseDTO.setPageInfo(pageInfo);
        return artworksResponseDTO;
    }

    public void addArtwork(ArtworkResponseDTO dto) {
        this.artworks.add(dto);
    }

}
