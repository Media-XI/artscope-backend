package com.example.codebase.domain.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.agora.document.AgoraDocument;
import com.example.codebase.domain.agora.dto.AgoraResponseDTO;
import com.example.codebase.domain.agora.dto.AgorasResponseDTO;
import com.example.codebase.domain.artwork.document.ArtworkDocument;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.exhibition.document.ExhibitionDocument;
import com.example.codebase.domain.exhibition.dto.ExhibitionPageInfoResponseDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionResponseDTO;
import com.example.codebase.domain.post.document.PostDocument;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.dto.PostsResponseDTO;
import com.example.codebase.domain.post.repository.PostRepository;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

    private NativeQuery makeNativeQuery(String keyword, PageRequest pageRequest, String... fields) {
        Query query = QueryBuilders.multiMatch()
                .fields(Arrays.asList(fields))
                .query(keyword)
                .build()._toQuery();
        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageRequest)
                .build();
    }

    public SearchResponseDTO totalSearch(String keyword, PageRequest pageRequest) {
        ArtworksResponseDTO artworksResponseDTO = new ArtworksResponseDTO();
        PostsResponseDTO postsResponseDTO = new PostsResponseDTO();

        NativeQuery artworkNativeQuery = makeNativeQuery(keyword, pageRequest, "title", "tags", "description");
        NativeQuery postNativeQuery = makeNativeQuery(keyword, pageRequest, "content");

        List<NativeQuery> nativeQueries = List.of(artworkNativeQuery, postNativeQuery);
        List<Class<?>> classes = List.of(ArtworkDocument.class, PostDocument.class);
        IndexCoordinates indexCoordinates = IndexCoordinates.of("artworks", "posts");

        List<SearchHits<?>> hits = elasticsearchOperations.multiSearch(nativeQueries, classes, indexCoordinates);
        long totalHits = 0;
        for (SearchHits<?> searchHits : hits) {
            totalHits += searchHits.getTotalHits();
            for (SearchHit<?> searchHit : searchHits) {
                Object content = searchHit.getContent();
                if (content instanceof ArtworkDocument) {
                    artworksResponseDTO.addArtwork(ArtworkResponseDTO.from((ArtworkDocument) content));
                    artworksResponseDTO.setPageInfo(PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), searchHits.getTotalHits()));
                } else if (content instanceof PostDocument) {
                    postsResponseDTO.addPost(PostResponseDTO.from((PostDocument) content));
                    postsResponseDTO.setPageInfo(PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), searchHits.getTotalHits()));
                }
            }
        }

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), totalHits);
        return SearchResponseDTO.of(artworksResponseDTO, postsResponseDTO, pageInfo);
    }

    public ArtworksResponseDTO artworkSearch(String keyword, PageRequest pageRequest) {
        NativeQuery nativeQuery = makeNativeQuery(keyword, pageRequest, "title", "tags", "description");
        SearchHits<ArtworkDocument> searched = elasticsearchOperations.search(nativeQuery, ArtworkDocument.class);

        List<ArtworkResponseDTO> dtos = searched.stream()
                .map(SearchHit::getContent)
                .map(ArtworkResponseDTO::from)
                .toList();

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                searched.getTotalHits());
        return ArtworksResponseDTO.of(dtos, pageInfo);
    }

    public PostsResponseDTO postSearch(String keyword, PageRequest pageRequest) {
        NativeQuery nativeQuery = makeNativeQuery(keyword, pageRequest, "content");
        SearchHits<PostDocument> searched = elasticsearchOperations.search(nativeQuery, PostDocument.class);

        List<PostResponseDTO> dtos = searched.stream()
                .map(SearchHit::getContent)
                .map(PostResponseDTO::from)
                .toList();

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                searched.getTotalHits());
        return PostsResponseDTO.of(dtos, pageInfo);
    }


    public AgorasResponseDTO agoraSearch(String keyword, PageRequest pageRequest) {
        NativeQuery nativeQuery = makeNativeQuery(keyword, pageRequest, "title", "content");
        SearchHits<AgoraDocument> searched = elasticsearchOperations.search(nativeQuery, AgoraDocument.class);

        List<AgoraResponseDTO> dtos = searched.stream()
                .map(SearchHit::getContent)
                .map(AgoraResponseDTO::from)
                .toList();

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                searched.getTotalHits());
        return AgorasResponseDTO.of(dtos, pageInfo);
    }

    public ExhibitionPageInfoResponseDTO eventSearch(String keyword, PageRequest pageRequest) {
        NativeQuery nativeQuery = makeNativeQuery(keyword, pageRequest, "title", "description");
        SearchHits<ExhibitionDocument> searched = elasticsearchOperations.search(nativeQuery, ExhibitionDocument.class);

        List<ExhibitionResponseDTO> dtos = searched.stream()
                .map(SearchHit::getContent)
                .map(ExhibitionResponseDTO::from)
                .toList();

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                searched.getTotalHits());
        return ExhibitionPageInfoResponseDTO.of(dtos, pageInfo);
    }
}
