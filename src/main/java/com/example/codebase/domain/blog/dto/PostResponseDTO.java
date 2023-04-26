package com.example.codebase.domain.blog.dto;

import com.example.codebase.domain.blog.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {

    private String title;

    private String content;

    private String author;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static PostResponseDTO of(Post newPost) {
        return PostResponseDTO.builder()
                .title(newPost.getTitle())
                .content(newPost.getContent())
                .author(newPost.getAuthor().getUsername())
                .createdTime(newPost.getCreatedTime())
                .updatedTime(newPost.getUpdatedTime())
                .build();
    }

    public static PostResponseDTO from(Post post) {
        return PostResponseDTO.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor().getUsername())
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .build();
    }
}
