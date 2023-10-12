package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.entity.Post;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostWithLikesResponseDTO extends PostResponseDTO {

    private List<PostLikeMemberDTO> likeMembers;

    public void setLikeMembers(List<PostLikeMemberDTO> likeMembers) {
        this.likeMembers = likeMembers;
    }

    public static PostWithLikesResponseDTO create (Post post, List<PostLikeMemberDTO> likeMembers) {
        PostWithLikesResponseDTO dto = new PostWithLikesResponseDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setViews(post.getViews());
        dto.setLikes(post.getLikes());
        dto.setComments(post.getComments());
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setAuthorName(post.getAuthor().getName());
        dto.setAuthorDescription(post.getAuthor().getIntroduction());
        dto.setAuthorProfileImageUrl(post.getAuthor().getPicture());
        dto.setCreatedTime(post.getCreatedTime());
        dto.setUpdatedTime(post.getUpdatedTime());
        dto.setLikeMembers(likeMembers);
        return dto;
    }

    public static PostWithLikesResponseDTO create(Post post, List<PostResponseDTO> comments, List<PostLikeMemberDTO> postLikeMemberDtos) {
        PostWithLikesResponseDTO dto = create(post, postLikeMemberDtos);
        dto.setCommentPosts(comments);
        return dto;
    }
}
