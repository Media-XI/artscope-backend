package com.example.codebase.domain.exhibition_artwork.dto;

import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExhibitionArtworksResponseDTO {
    private ResponseExhibitionDTO exhibition;
    private List<ArtworkResponseDTO> artworks;
    public static ExhibitionArtworksResponseDTO from(Exhibition exhibition, List<ArtworkResponseDTO> artworks) {
        ExhibitionArtworksResponseDTO dto = new ExhibitionArtworksResponseDTO();
        dto.setExhibition(ResponseExhibitionDTO.from(exhibition));
        dto.setArtworks(artworks);
        return dto;
    }
}
