package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExhibitionMediaResponseDTO {
    private String mediaType;

    private String mediaUrl;

    public static ExhibitionMediaResponseDTO from(ExhibitionMedia exhibitionMedia) {
        ExhibitionMediaResponseDTO dto = new ExhibitionMediaResponseDTO();
        dto.setMediaType(exhibitionMedia.getExhibtionMediaType().name());
        dto.setMediaUrl(exhibitionMedia.getMediaUrl());
        return dto;
    }
}
