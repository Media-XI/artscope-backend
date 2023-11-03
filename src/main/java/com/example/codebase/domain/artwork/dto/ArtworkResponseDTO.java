package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkResponseDTO {

    private Long id;

    private String title;

    private List<String> tags;

    private String description;

    private Integer views;

    private Integer likes;

    private Integer comments;

    private String authorUsername;

    private String authorName;

    private ArtworkMediaResponseDTO thumbnail;

    private List<ArtworkMediaResponseDTO> artworkMedias;

    private List<ArtworkCommentResponseDTO> artworkComments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ArtworkResponseDTO from(Artwork artwork) {
        List<ArtworkMedia> artworkMedia = artwork.getArtworkMedia();

        ArtworkMediaResponseDTO thumbnail =
                artworkMedia.stream().findFirst().map(ArtworkMediaResponseDTO::from).orElse(null);

        List<ArtworkMediaResponseDTO> artworkMediaResponseDTOS =
                artworkMedia.stream()
                        .skip(1)
                        .map(ArtworkMediaResponseDTO::from)
                        .collect(Collectors.toList());

        List<String> tags = null;
        if (Optional.ofNullable(artwork.getTags()).isPresent()) {
            String[] split = artwork.getTags().split(",");
            tags = List.of(split);
        }

        return ArtworkResponseDTO.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .tags(tags)
                .description(artwork.getDescription())
                .views(artwork.getViews())
                .likes(artwork.getLikes())
                .comments(artwork.getComments())
                .authorName(artwork.getMember().getName())
                .authorUsername(artwork.getMember().getUsername())
                .thumbnail(thumbnail)
                .artworkMedias(artworkMediaResponseDTOS)
                .createdTime(artwork.getCreatedTime())
                .updatedTime(artwork.getUpdatedTime())
                .build();
    }

    public static ArtworkResponseDTO of(
            Artwork artwork, List<ArtworkCommentResponseDTO> artworkComments) {
        ArtworkResponseDTO artworkResponseDTO = from(artwork);
        artworkResponseDTO.setArtworkComments(artworkComments);
        return artworkResponseDTO;
    }
}
