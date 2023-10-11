package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import lombok.*;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostWithLikesResponseDTO extends PostResponseDTO {

    private List<PostLikeMemberDTO> likeMembers;

    public void setLikeMembers(List<PostLikeMemberDTO> likeMembers) {
        this.likeMembers = likeMembers;
    }

    public static PostWithLikesResponseDTO of (Post post, List<PostLikeMemberDTO> likeMembers) {
        PostWithLikesResponseDTO dto = new PostWithLikesResponseDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setViews(post.getViews());
        dto.setLikes(post.getLikes());
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setAuthorName(post.getAuthor().getName());
        dto.setAuthorDescription(post.getAuthor().getIntroduction());
        dto.setAuthorProfileImageUrl(post.getAuthor().getPicture());
        dto.setCreatedTime(post.getCreatedTime());
        dto.setUpdatedTime(post.getUpdatedTime());
        dto.setLikeMembers(likeMembers);
        return dto;
    }
}
