package com.example.codebase.domain.search;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    private final PostRepository postRepository;

    private final ArtworkRepository artworkRepository;

    @Autowired
    public SearchService(PostRepository postRepository, ArtworkRepository artworkRepository) {
        this.postRepository = postRepository;
        this.artworkRepository = artworkRepository;
    }

    public SearchResponseDTO totalSearch(String keyword, PageRequest pageRequest) {
        // 아트워크 검색
        Page<Artwork> artworks = artworkRepository.findAllByKeywordContaining(keyword, pageRequest);
        PageInfo pageInfoArtwork = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                artworks.getTotalPages(), artworks.getTotalElements());

        List<ArtworkResponseDTO> dtos = artworks.stream()
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());
        ArtworksResponseDTO artworksResponseDTO = ArtworksResponseDTO.of(dtos, pageInfoArtwork);

        // Post 검색
        Page<Post> posts = postRepository.findAllByKeywordContaining(keyword, pageRequest);
        PageInfo pageInfoPost = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                posts.getTotalPages(), posts.getTotalElements());

        List<PostResponseDTO> postResponseDTOS = posts.stream()
                .map(PostResponseDTO::from)
                .collect(Collectors.toList());
        PostsResponseDTO postsResponseDTO = PostsResponseDTO.of(postResponseDTOS, pageInfoPost);

        return SearchResponseDTO.of(artworksResponseDTO, postsResponseDTO);
    }
}
