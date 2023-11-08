package com.example.codebase.domain.post.dto;

import com.example.codebase.controller.dto.PageInfo;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostsResponseDTO {

    List<PostResponseDTO> posts;

    PageInfo pageInfo;

    public static PostsResponseDTO of(List<PostResponseDTO> dtos, PageInfo pageInfo) {
        PostsResponseDTO responseDTO = new PostsResponseDTO();
        responseDTO.posts = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }
}
