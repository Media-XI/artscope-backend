package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.document.PostDocument;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Builder.Default
    protected Boolean isLiked = false;

    protected String authorUsername;

    protected String authorName;

    protected String authorIntroduction;

    protected String authorProfileImageUrl;

    protected String authorCompanyRole;

    protected String authorCompanyName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    protected LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    protected LocalDateTime updatedTime;

    protected List<PostCommentResponseDTO> commentPosts;

    protected List<PostMediaResponseDTO> medias;

    protected PostResponseDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.views = post.getViews();
        this.likes = post.getLikes();
        this.comments = post.getComments();
        this.authorUsername = post.getAuthor().getUsername();
        this.authorName = post.getAuthor().getName();
        this.authorIntroduction = post.getAuthor().getIntroduction();
        this.authorProfileImageUrl = post.getAuthor().getPicture();
        this.createdTime = post.getCreatedTime();
        this.updatedTime = post.getUpdatedTime();
        this.isLiked = false;

        this.medias = post.getPostMedias().stream()
                .map(PostMediaResponseDTO::from)
                .collect(Collectors.toList());

        if (post.getPostComment() != null) {
            this.commentPosts = post.getPostComment().stream()
                    .filter(comment -> comment.getParent() == null)
                    .filter(comment -> comment.getChildComments() != null)
                    .map(PostCommentResponseDTO::from)
                    .collect(Collectors.toList());
        }

        // TODO : 후처리 리팩터링
        if (post.getAuthor().getCompanyName() != null) {
            this.authorCompanyName = post.getAuthor().getCompanyName();
        }

        if (post.getAuthor().getCompanyRole() != null) {
            this.authorCompanyRole = post.getAuthor().getCompanyRole();
        }
    }

    public static PostResponseDTO from(Post post) {
        PostResponseDTO response =
                PostResponseDTO.builder()
                        .id(post.getId())
                        .content(post.getContent())
                        .views(post.getViews())
                        .likes(post.getLikes())
                        .comments(post.getComments())
                        .authorUsername(post.getAuthor().getUsername())
                        .authorName(post.getAuthor().getName())
                        .authorIntroduction(post.getAuthor().getIntroduction()) // TODO : introduction이 맞는지 확인
                        .authorProfileImageUrl(post.getAuthor().getPicture())
                        .createdTime(post.getCreatedTime())
                        .updatedTime(post.getUpdatedTime())
                        .build();

        // TODO: 후처리 리팩터링
        if (post.getPostComment() != null) {
            List<PostCommentResponseDTO> commentResponse =
                    post.getPostComment().stream()
                            .filter(comment -> comment.getParent() == null)
                            .filter(comment -> comment.getChildComments() != null)
                            .map(PostCommentResponseDTO::from)
                            .collect(Collectors.toList());

            response.setCommentPosts(commentResponse);
        }

        if (post.getAuthor().getCompanyName() != null) {
            response.setAuthorCompanyName(post.getAuthor().getCompanyName());
        }

        if (post.getAuthor().getCompanyRole() != null) {
            response.setAuthorCompanyRole(post.getAuthor().getCompanyRole());
        }

        List<PostMediaResponseDTO> mediaResponse =
                post.getPostMedias().stream()
                        .map(PostMediaResponseDTO::from)
                        .collect(Collectors.toList());
        response.setMedias(mediaResponse);

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

    public static PostResponseDTO from(PostDocument postDocument) {
        List<PostMediaResponseDTO> medias = new ArrayList<>();

        if (postDocument.getMediaUrl() != null) {
            medias.add(PostMediaResponseDTO.builder()
                    .mediaUrl(postDocument.getMediaUrl())
                    .build()
            );
        }

        return PostResponseDTO.builder()
                .id(postDocument.getId())
                .content(postDocument.getContent())
                .authorName(postDocument.getName())
                .authorCompanyName(postDocument.getCompanyName())
                .authorCompanyRole(postDocument.getCompanyRole())
                .medias(medias)
                .createdTime(postDocument.getCreatedTime())
                .updatedTime(postDocument.getUpdatedTime())
                .build();
    }
}