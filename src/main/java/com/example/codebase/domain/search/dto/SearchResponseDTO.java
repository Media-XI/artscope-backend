package com.example.codebase.domain.search.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDTO {

    private ArtworksResponseDTO searchArtworks;

    private PostsResponseDTO searchPosts;

    private PageInfo pageInfo;

    public static SearchResponseDTO of(ArtworksResponseDTO artworks, PostsResponseDTO posts) {
        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();
        searchResponseDTO.searchArtworks = artworks;
        searchResponseDTO.searchPosts = posts;
        return searchResponseDTO;
    }

    public static SearchResponseDTO of (ArtworksResponseDTO artworks, PostsResponseDTO posts, PageInfo pageInfo) {
        SearchResponseDTO searchResponseDTO = of(artworks, posts);
        searchResponseDTO.pageInfo = pageInfo;
        return searchResponseDTO;
    }
}
