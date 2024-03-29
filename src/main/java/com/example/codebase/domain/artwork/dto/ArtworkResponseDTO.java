package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.document.ArtworkDocument;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private String authorProfileImage;

    private String authorIntroduction;

    private String authorCompanyName;

    private String authorCompanyRole;

    private ArtworkMediaResponseDTO thumbnail;

    private List<ArtworkMediaResponseDTO> artworkMedias;

    private List<ArtworkCommentResponseDTO> artworkComments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ArtworkResponseDTO from(ArtworkDocument artworkDocument) {
        ArtworkResponseDTO dto = ArtworkResponseDTO.builder()
                .id(artworkDocument.getId())
                .title(artworkDocument.getTitle())
                .tags(Collections.emptyList())
                .authorName(artworkDocument.getName())
                .authorCompanyName(artworkDocument.getCompanyName())
                .authorCompanyRole(artworkDocument.getCompanyRole())
                .thumbnail(ArtworkMediaResponseDTO.builder().mediaUrl(artworkDocument.getMediaUrl()).build())
                .description(artworkDocument.getDescription())
                .createdTime(artworkDocument.getCreatedTime())
                .updatedTime(artworkDocument.getUpdatedTime())
                .build();

        if (artworkDocument.getTags() != null) {
            String[] split = artworkDocument.getTags().split(",");
            dto.setTags(List.of(split));
        }

        return dto;
    }

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
                .authorIntroduction(artwork.getMember().getIntroduction())
                .authorProfileImage(artwork.getMember().getPicture() != null ? artwork.getMember().getPicture() : null)
                .authorCompanyName(
                        artwork.getMember().getCompanyName() != null ? artwork.getMember().getCompanyName() : null)
                .authorCompanyRole(
                        artwork.getMember().getCompanyRole() != null ? artwork.getMember().getCompanyRole() : null)
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
