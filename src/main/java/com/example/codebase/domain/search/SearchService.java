package com.example.codebase.domain.search;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.document.ArtworkDocument;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final PostRepository postRepository;
    private final ArtworkRepository artworkRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public SearchService(PostRepository postRepository, ArtworkRepository artworkRepository, ElasticsearchOperations elasticsearchOperations) {
        this.postRepository = postRepository;
        this.artworkRepository = artworkRepository;
        this.elasticsearchOperations = elasticsearchOperations;
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

    public List<ArtworkResponseDTO> artworkSearch(String keyword, PageRequest pageRequest) {
        Query query = QueryBuilders.multiMatch()
                .fields("title", "tags", "description")
                .query(keyword)
                .build()._toQuery();
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .build();
        SearchHits<ArtworkDocument> searched = elasticsearchOperations.search(nativeQuery, ArtworkDocument.class);
        return searched
                .stream()
                .map(SearchHit::getContent)
                .map(ArtworkResponseDTO::from)
                .toList();
    }
}
