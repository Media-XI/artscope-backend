package com.example.codebase.domain.exhibition_artwork.dto;

import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtwork;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtworkStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ExhibitionArtworkResponseDTO {
    private Long id;
    private ResponseExhibitionDTO exhibition;
    private ArtworkResponseDTO artwork;
    private ExhibitionArtworkStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ExhibitionArtworkResponseDTO from(ExhibitionArtwork save) {
        ExhibitionArtworkResponseDTO dto = new ExhibitionArtworkResponseDTO();
        dto.setId(save.getId());
        dto.setExhibition(ResponseExhibitionDTO.from(save.getExhibition()));
        dto.setArtwork(ArtworkResponseDTO.from(save.getArtwork()));
        dto.setStatus(save.getStatus());
        dto.setCreatedTime(save.getCreatedTime());
        dto.setUpdatedTime(save.getUpdatedTime());
        return dto;
    }
}
