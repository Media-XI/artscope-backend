package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.ArtworkComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkCommentResponseDTO {

    protected String authorUsername;
    protected String authorName;
    protected String authorDescription;
    protected String authorProfileImageUrl;
    private Long id;
    private String content;
    private Long parentCommentId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ArtworkCommentResponseDTO from(ArtworkComment artworkComment) {
        Long parentId = Optional.ofNullable(artworkComment.getParentComment())
            .map(ArtworkComment::getId)
            .orElse(null);

        return ArtworkCommentResponseDTO.builder()
            .id(artworkComment.getId())
            .content(artworkComment.getContent())
            .authorUsername(artworkComment.getAuthor().getUsername())
            .authorName(artworkComment.getAuthor().getName())
            .authorDescription(artworkComment.getAuthor().getIntroduction())
            .authorProfileImageUrl(artworkComment.getAuthor().getPicture())
            .parentCommentId(parentId)
            .createdTime(artworkComment.getCreatedTime())
            .updatedTime(artworkComment.getUpdatedTime())
            .build();
    }
}
