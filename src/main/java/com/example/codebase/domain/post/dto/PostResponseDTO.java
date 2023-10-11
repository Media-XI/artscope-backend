package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {

    protected Long id;

    protected String content;

    protected Integer views;

    protected Integer likes;

    @Builder.Default
    protected Boolean isLiked = false;

    protected String authorUsername;

    protected String authorName;

    protected String authorDescription;

    protected String authorProfileImageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    protected LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    protected LocalDateTime updatedTime;


    public static PostResponseDTO from(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .views(post.getViews())
                .likes(post.getLikes())
                .authorUsername(post.getAuthor().getUsername())
                .authorName(post.getAuthor().getName())
                .authorDescription(post.getAuthor().getIntroduction()) // TODO : introduction이 맞는지 확인
                .authorProfileImageUrl(post.getAuthor().getPicture())
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .build();
    }

    public static PostResponseDTO from(PostWithIsLiked post) {
        PostResponseDTO dto = from(post.getPost());
        dto.setIsLiked(post.getIsLiked());
        return dto;
    }

    public static PostResponseDTO of (Post post, Boolean isLiked) {
        PostResponseDTO dto = from(post);
        dto.setIsLiked(isLiked);
        return dto;
    }
}
