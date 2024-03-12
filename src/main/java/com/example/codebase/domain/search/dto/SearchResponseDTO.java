package com.example.codebase.domain.search.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.event.dto.EventsResponseDTO;
import com.example.codebase.domain.agora.dto.AgorasResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchResponseDTO {

    private ArtworksResponseDTO searchArtworks;

    private PostsResponseDTO searchPosts;

    private AgorasResponseDTO searchAgoras;

    private EventsResponseDTO searchEvents;

    private PageInfo pageInfo;

    private static SearchResponseDTO of(ArtworksResponseDTO artworks, PostsResponseDTO posts, AgorasResponseDTO agoras, EventsResponseDTO events) {
        SearchResponseDTO searchResponseDTO = new SearchResponseDTO();
        searchResponseDTO.searchArtworks = artworks;
        searchResponseDTO.searchPosts = posts;
        searchResponseDTO.searchAgoras = agoras;
        searchResponseDTO.searchEvents = events;
        return searchResponseDTO;
    }

    public static SearchResponseDTO of (ArtworksResponseDTO artworks, PostsResponseDTO posts, AgorasResponseDTO agoras, EventsResponseDTO events, PageInfo pageInfo) {
        SearchResponseDTO searchResponseDTO = of(artworks, posts, agoras, events);
        searchResponseDTO.pageInfo = pageInfo;
        return searchResponseDTO;
    }

}
