package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostWithLikesResponseDTO extends PostResponseDTO {

    private List<PostLikeMemberDTO> likeMembers;

    public PostWithLikesResponseDTO(Post post) {
        super(post);
    }

    public static PostWithLikesResponseDTO create(Post post, List<PostLikeMemberDTO> likeMembers) {
        PostWithLikesResponseDTO dto = new PostWithLikesResponseDTO(post);
        dto.setLikeMembers(likeMembers);
        return dto;
    }

    public void setLikeMembers(List<PostLikeMemberDTO> likeMembers) {
        this.likeMembers = likeMembers;
    }
}
