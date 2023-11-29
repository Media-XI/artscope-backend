package com.example.codebase.domain.search.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.agora.dto.AgorasResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionPageInfoResponseDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDTO {

    private ArtworksResponseDTO searchArtworks;

    private PostsResponseDTO searchPosts;

    private AgorasResponseDTO searchAgoras;

    private ExhibitionPageInfoResponseDTO searchExhibitions;

    private PageInfo pageInfo;

    private static SearchResponseDTO of(ArtworksResponseDTO artworks, PostsResponseDTO posts, AgorasResponseDTO agoras, ExhibitionPageInfoResponseDTO exhibitions) {
        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();
        searchResponseDTO.searchArtworks = artworks;
        searchResponseDTO.searchPosts = posts;
        searchResponseDTO.searchAgoras = agoras;
        searchResponseDTO.searchExhibitions = exhibitions;
        return searchResponseDTO;
    }

    public static SearchResponseDTO of (ArtworksResponseDTO artworks, PostsResponseDTO posts, AgorasResponseDTO agoras, ExhibitionPageInfoResponseDTO exhibitions, PageInfo pageInfo) {
        SearchResponseDTO searchResponseDTO = of(artworks, posts, agoras, exhibitions);
        searchResponseDTO.pageInfo = pageInfo;
        return searchResponseDTO;
    }

}
