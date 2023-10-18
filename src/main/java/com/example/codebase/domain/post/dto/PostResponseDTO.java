package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
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
public class PostResponseDTO {

    protected Long id;

    protected String content;

    protected Integer views;

    protected Integer likes;

    protected Integer comments;

    protected String mentionUsername;

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

    protected List<PostCommentResponseDTO> commentPosts;

    public static PostResponseDTO from(Post post) {

        PostResponseDTO response = PostResponseDTO.builder().id(post.getId()).content(post.getContent()).views(post.getViews()).likes(post.getLikes()).comments(post.getComments()).authorUsername(post.getAuthor().getUsername()).authorName(post.getAuthor().getName()).authorDescription(post.getAuthor().getIntroduction()) // TODO : introduction이 맞는지 확인
                .authorProfileImageUrl(post.getAuthor().getPicture()).createdTime(post.getCreatedTime()).updatedTime(post.getUpdatedTime()).build();

        if (post.getPostComment() != null) {
            List<PostCommentResponseDTO> commentResponse = post.getPostComment().stream()
                    .filter(comment -> comment.getParent() == null)
                    .filter(comment -> comment.getChildComments() != null)
                    .map(PostCommentResponseDTO::from)
                    .collect(Collectors.toList());

            response.setCommentPosts(commentResponse);
        }

        return response;
    }

    public static PostResponseDTO from(PostWithIsLiked post) {
        PostResponseDTO dto = from(post.getPost());
        dto.setIsLiked(post.getIsLiked());
        return dto;
    }

    public static PostResponseDTO of(Post post, Boolean isLiked) {
        PostResponseDTO dto = from(post);
        dto.setIsLiked(isLiked);
        return dto;
    }
}
