package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import lombok.*;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class PostWithLikesResponseDTO extends PostResponseDTO {

    List<PostLikeMemberDTO> likeMembers;
    public static PostWithLikesResponseDTO of (Post post, List<PostLikeMemberDTO> likeMembers) {
        PostWithLikesResponseDTO postResponseDTO = (PostWithLikesResponseDTO) from(post);
        postResponseDTO.likeMembers = likeMembers;
        return postResponseDTO;
    }
}
