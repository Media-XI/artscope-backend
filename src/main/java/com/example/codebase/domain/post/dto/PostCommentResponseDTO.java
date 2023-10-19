package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.entity.PostComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentResponseDTO {

  protected Long id;

  protected String content;

  protected Integer likes;

  protected Integer comments;

  protected String mentionUsername;

  protected String authorUsername;

  protected String authorName;

  protected String authorProfileImageUrl;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  protected LocalDateTime createdTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  protected LocalDateTime updatedTime;

  protected Long parentCommentId;

  protected List<PostCommentResponseDTO> childComments;

  public static PostCommentResponseDTO from(PostComment comment) {
    Long parentId = Optional.ofNullable(comment.getParent()).map(PostComment::getId).orElse(null);

    PostCommentResponseDTO response =
        PostCommentResponseDTO.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .comments(comment.getComments())
            .mentionUsername(comment.getMentionUsername())
            .authorUsername(comment.getAuthor().getUsername())
            .authorName(comment.getAuthor().getName())
            .authorProfileImageUrl(comment.getAuthor().getPicture())
            .createdTime(comment.getCreatedTime())
            .updatedTime(comment.getUpdatedTime())
            .parentCommentId(parentId)
            .build();

    if (Optional.ofNullable(comment.getChildComments()).isPresent()) {
      List<PostCommentResponseDTO> childComments =
          comment.getChildComments().stream()
              .map(PostCommentResponseDTO::from)
              .collect(Collectors.toList());
      response.setChildComments(childComments);
    }

    return response;
  }
}
