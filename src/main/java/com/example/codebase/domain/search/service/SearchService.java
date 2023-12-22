package com.example.codebase.domain.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.Event.dto.EventResponseDTO;
import com.example.codebase.domain.Event.dto.EventsResponseDTO;
import com.example.codebase.domain.agora.document.AgoraDocument;
import com.example.codebase.domain.agora.dto.AgoraResponseDTO;
import com.example.codebase.domain.agora.dto.AgorasResponseDTO;
import com.example.codebase.domain.artwork.document.ArtworkDocument;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.dto.ArtworksResponseDTO;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.Event.document.EventDocument;
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

    public SearchResponseDTO searchTotal(String keyword, PageRequest pageRequest) {
        ArtworksResponseDTO artworksResponseDTO = new ArtworksResponseDTO();
        PostsResponseDTO postsResponseDTO = new PostsResponseDTO();
        AgorasResponseDTO agorasResponseDTO = new AgorasResponseDTO();
        EventsResponseDTO eventsResponseDTO = new EventsResponseDTO();

        NativeQuery artworkNativeQuery = makeNativeQuery(keyword, pageRequest, "title", "tags", "description");
        NativeQuery postNativeQuery = makeNativeQuery(keyword, pageRequest, "content");
        NativeQuery agoraNativeQuery = makeNativeQuery(keyword, pageRequest, "title", "content");
        NativeQuery eventNativeQuery = makeNativeQuery(keyword, pageRequest, "title", "description");

        List<NativeQuery> nativeQueries = List.of(artworkNativeQuery, postNativeQuery, agoraNativeQuery, eventNativeQuery);
        List<Class<?>> classes = List.of(ArtworkDocument.class, PostDocument.class, AgoraDocument.class, EventDocument.class);

        List<SearchHits<?>> hits = elasticsearchOperations.multiSearch(nativeQueries, classes);

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
                } else if (content instanceof AgoraDocument) {
                    agorasResponseDTO.addAgora(AgoraResponseDTO.from((AgoraDocument) content));
                    agorasResponseDTO.setPageInfo(PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), searchHits.getTotalHits()));
                } else if (content instanceof EventDocument) {
                    eventsResponseDTO.addEvent(EventResponseDTO.from((EventDocument) content));
                    eventsResponseDTO.setPageInfo(PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), searchHits.getTotalHits()));
                }
            }
        }

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), totalHits);
        return SearchResponseDTO.of(artworksResponseDTO, postsResponseDTO, agorasResponseDTO, eventsResponseDTO, pageInfo);
    }

    public ArtworksResponseDTO searchArtwork(String keyword, PageRequest pageRequest) {
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

    public PostsResponseDTO searchPost(String keyword, PageRequest pageRequest) {
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


    public AgorasResponseDTO searchAgora(String keyword, PageRequest pageRequest) {
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

    public EventsResponseDTO searchEvent(String keyword, PageRequest pageRequest) {
        NativeQuery nativeQuery = makeNativeQuery(keyword, pageRequest, "title", "description");
        SearchHits<EventDocument> searched = elasticsearchOperations.search(nativeQuery, EventDocument.class);

        List<EventResponseDTO> dtos = searched.stream()
                .map(SearchHit::getContent)
                .map(EventResponseDTO::from)
                .toList();

        PageInfo pageInfo = PageInfo.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                searched.getTotalHits());
        return EventsResponseDTO.of(dtos, pageInfo);
    }
}
