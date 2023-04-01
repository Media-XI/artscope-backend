package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
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

    private ArtworkMediaResponseDTO thumbnail;

    private List<ArtworkMediaResponseDTO> artworkMedias;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

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
                .thumbnail(artworkMediaResponseDTOS.get(0))
                .artworkMedias(artworkMediaResponseDTOS)
                .createdTime(artwork.getCreatedTime())
                .updatedTime(artwork.getUpdatedTime())
                .build();
    }
}
