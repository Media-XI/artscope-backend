package com.example.codebase.domain.post.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostsResponseDTO {

    List<PostResponseDTO> posts = new ArrayList<>();

    PageInfo pageInfo = new PageInfo();

    public static PostsResponseDTO of(List<PostResponseDTO> dtos, PageInfo pageInfo) {
        PostsResponseDTO responseDTO = new PostsResponseDTO();
        responseDTO.posts = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }

    public void addPost(PostResponseDTO postResponseDTO) {
        this.posts.add(postResponseDTO);
    }
}
