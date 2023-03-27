package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String member;
    private List<ArtworkMediaResponseDTO> artworkMedias;

    public static ArtworkResponseDTO from(Artwork artwork) {
        List<ArtworkMedia> artworkMedia = artwork.getArtworkMedia();
        List<ArtworkMediaResponseDTO> artworkMediaResponseDTOS = artworkMedia.stream()
                .map(ArtworkMediaResponseDTO::from)
                .collect(Collectors.toList());

        return ArtworkResponseDTO.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .member(artwork.getMember().getUsername())
                .artworkMedias(artworkMediaResponseDTOS)
                .build();
    }
}
