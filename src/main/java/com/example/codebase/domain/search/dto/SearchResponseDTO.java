package com.example.codebase.domain.search.dto;

import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDTO {

    private ArtworksResponseDTO searchArtworks;

    private PostsResponseDTO searchPosts;

    public static SearchResponseDTO of(ArtworksResponseDTO artworks, PostsResponseDTO posts) {
        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();
        searchResponseDTO.searchArtworks = artworks;
        searchResponseDTO.searchPosts = posts;
        return searchResponseDTO;
    }
}
