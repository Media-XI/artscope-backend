package com.example.codebase.domain.feed.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.feed.dto.FeedItemResponseDto;
import com.example.codebase.domain.feed.dto.FeedResponseDto;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private final ArtworkRepository artworkRepository;
    private final PostRepository postRepository;
    private final ExhibitionRepository exhibitionRepository;
    @Autowired
    public FeedService(ArtworkRepository artworkRepository, PostRepository postRepository, ExhibitionRepository exhibitionRepository) {
        this.artworkRepository = artworkRepository;
        this.postRepository = postRepository;
        this.exhibitionRepository = exhibitionRepository;
    }

    @Transactional()
    public FeedResponseDto createFeed(int page, int size) {
        List<FeedItemResponseDto> feedItems = new ArrayList<>();
        int totalPages = 0;
        int totalElements = 0;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size / 3, sort);

        // 아트워크 조회
        Page<Artwork> artworks = artworkRepository.findAll(pageRequest);
        totalElements += artworks.getTotalElements();
        totalPages += artworks.getTotalPages();

        List<FeedItemResponseDto> artworkItems = artworks
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(artworkItems);

        // Post 조회
        Page<Post> posts = postRepository.findAll(pageRequest);
        totalElements += posts.getTotalElements();
        totalPages += posts.getTotalPages();

        List<FeedItemResponseDto> postItems = posts
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(postItems);

        // 전시 조회
        Page<Exhibition> exhibitions = exhibitionRepository.findAll(pageRequest);
        totalElements += exhibitions.getTotalElements();
        totalPages += exhibitions.getTotalPages();

        List<FeedItemResponseDto> exhibitionItems = exhibitions
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(exhibitionItems);

        PageInfo pageInfo = PageInfo.of(page, size, totalPages / 3, totalElements);
        return FeedResponseDto.of(feedItems, pageInfo);
    }
}
