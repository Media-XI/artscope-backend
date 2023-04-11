package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkMediaResponseDTO {
    private Long id;
    private String mediaType;
    private String mediaUrl;

    private Integer imageWidth;

    private Integer imageHeight;

    private String description;

    public static ArtworkMediaResponseDTO from (ArtworkMedia artworkMedia) {
        return ArtworkMediaResponseDTO.builder()
                .id(artworkMedia.getId())
                .mediaType(artworkMedia.getArtworkMediaType().name())
                .mediaUrl(artworkMedia.getMediaUrl())
                .imageWidth(artworkMedia.getImageWidth())
                .imageHeight(artworkMedia.getImageHeight())
                .description(artworkMedia.getDescription())
                .build();
    }
}
