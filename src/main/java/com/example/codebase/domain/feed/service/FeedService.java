package com.example.codebase.domain.feed.service;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.feed.dto.FeedItemResponseDto;
import com.example.codebase.domain.feed.dto.FeedResponseDto;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import com.example.codebase.domain.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private final ArtworkRepository artworkRepository;
    private final PostRepository postRepository;
    private final ExhibitionRepository exhibitionRepository;

    private final MemberRepository memberRepository;

    @Autowired
    public FeedService(ArtworkRepository artworkRepository, PostRepository postRepository, ExhibitionRepository exhibitionRepository, MemberRepository memberRepository) {
        this.artworkRepository = artworkRepository;
        this.postRepository = postRepository;
        this.exhibitionRepository = exhibitionRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public FeedResponseDto createFeed(int page, int size) {
        List<FeedItemResponseDto> feedItems = new ArrayList<>();

        Sort sort = Sort.by("createdTime").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // 아트워크 조회
        Page<Artwork> artworks = artworkRepository.findAll(pageRequest);

        List<FeedItemResponseDto> artworkItems = artworks
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(artworkItems);

        // Post 조회
        Page<Post> posts = postRepository.findAll(pageRequest);

        List<FeedItemResponseDto> postItems = posts
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(postItems);

        // 전시 조회
        Page<Exhibition> exhibitions = exhibitionRepository.findAll(pageRequest);

        List<FeedItemResponseDto> exhibitionItems = exhibitions
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(exhibitionItems);

        // application sort
        feedItems.sort((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()));

        boolean hasNext = artworks.hasNext() || posts.hasNext() || exhibitions.hasNext();
        return FeedResponseDto.of(feedItems,hasNext);
    }

    @Transactional(readOnly = true)
    public FeedResponseDto createFeedLoginUser(String username, int page, int size) {
        List<FeedItemResponseDto> feedItems = new ArrayList<>();

        Sort sort = Sort.by("createdTime").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        // 아트워크 조회
        Page<ArtworkWithIsLike> artworks = artworkRepository.findAllWithIsLiked(member, pageRequest);

        List<FeedItemResponseDto> artworkItems = artworks
                .stream()
                .map((artwork) -> FeedItemResponseDto.of(artwork.getArtwork(), artwork.getIsLike()))
                .collect(Collectors.toList());
        feedItems.addAll(artworkItems);

        // Post 조회
        Page<PostWithIsLiked> posts = postRepository.findAllWithIsLiked(member, pageRequest);

        List<FeedItemResponseDto> postItems = posts
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(postItems);

        // 전시 조회
        Page<Exhibition> exhibitions = exhibitionRepository.findAll(pageRequest);

        List<FeedItemResponseDto> exhibitionItems = exhibitions
                .stream()
                .map(FeedItemResponseDto::from)
                .collect(Collectors.toList());
        feedItems.addAll(exhibitionItems);

        // application sort
        feedItems.sort((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()));

        boolean hasNext = artworks.hasNext() || posts.hasNext() || exhibitions.hasNext();
        return FeedResponseDto.of(feedItems,hasNext);
    }
}
