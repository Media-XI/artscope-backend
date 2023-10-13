package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkCommentResponseDTO {

    private Long id;

    private String content;

    protected String authorUsername;

    protected String authorName;

    protected String authorDescription;

    protected String authorProfileImageUrl;

    private Long parentCommentId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ArtworkCommentResponseDTO from(ArtworkComment artworkComment) {
        // TODO : NPE 방지 (상위 댓글은 ParentID가 없음)
        ArtworkComment parentComment = artworkComment.getParentComment() == null ? artworkComment : artworkComment.getParentComment();
        Long parentCommentId = parentComment.getId() == null ? null : parentComment.getId();

        return ArtworkCommentResponseDTO.builder()
                .id(artworkComment.getId())
                .content(artworkComment.getContent())
                .authorUsername(artworkComment.getMember().getUsername())
                .authorName(artworkComment.getMember().getName())
                .authorDescription(artworkComment.getMember().getIntroduction())
                .authorProfileImageUrl(artworkComment.getMember().getPicture())
                .parentCommentId(parentCommentId)
                .createdTime(artworkComment.getCreatedTime())
                .updatedTime(artworkComment.getUpdatedTime())
                .build();
    }
}
