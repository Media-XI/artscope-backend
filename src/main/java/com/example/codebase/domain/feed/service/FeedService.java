package com.example.codebase.domain.feed.service;

import com.example.codebase.domain.event.entity.Event;
import com.example.codebase.domain.event.repository.EventRepository;
import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraWithParticipant;
import com.example.codebase.domain.agora.repository.AgoraRepository;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.feed.dto.FeedItemResponseDto;
import com.example.codebase.domain.feed.dto.FeedResponseDto;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.dto.PostResponseDTO;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import com.example.codebase.domain.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedService {

    private final ArtworkRepository artworkRepository;
    private final PostRepository postRepository;
    private final EventRepository eventRepository;
    private final AgoraRepository agoraRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public FeedService(ArtworkRepository artworkRepository, PostRepository postRepository,
                       EventRepository eventRepository, AgoraRepository agoraRepository, MemberRepository memberRepository) {
        this.artworkRepository = artworkRepository;
        this.postRepository = postRepository;
        this.eventRepository = eventRepository;
        this.agoraRepository = agoraRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public FeedResponseDto createFeed(int page, int size) {

        Sort sort = Sort.by("createdTime").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // 아트워크 조회
        Page<Artwork> artworks = artworkRepository.findAll(pageRequest);

        List<FeedItemResponseDto> artworkItems = artworks
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        List<FeedItemResponseDto> feedItems = new ArrayList<>(artworkItems);

        // Post 조회
        Page<Post> posts = postRepository.findAll(pageRequest);

        List<FeedItemResponseDto> postItems = posts
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        feedItems.addAll(postItems);

        // 전시 조회
        Page<Event> events = eventRepository.findAll(pageRequest);

        List<FeedItemResponseDto> eventItems = events
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        feedItems.addAll(eventItems);

        // 아고라 조회
        Page<Agora> agoras = agoraRepository.findAll(pageRequest);

        List<FeedItemResponseDto> agoraItems = agoras
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        feedItems.addAll(agoraItems);

        // application sort
        feedItems.sort((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()));

        boolean hasNext = artworks.hasNext() || posts.hasNext() || events.hasNext() || agoras.hasNext();
        return FeedResponseDto.of(feedItems, hasNext);
    }

    @Transactional(readOnly = true)
    public FeedResponseDto createFeedLoginUser(String username, int page, int size) {

        Sort sort = Sort.by("createdTime").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        // 아트워크 조회
        Page<ArtworkWithIsLike> artworks = artworkRepository.findAllWithIsLiked(member, pageRequest);

        List<FeedItemResponseDto> artworkItems = artworks
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        List<FeedItemResponseDto> feedItems = new ArrayList<>(artworkItems);

        // Post 조회
        Page<PostWithIsLiked> posts = postRepository.findAllWithIsLiked(member, pageRequest);

        List<FeedItemResponseDto> postItems = posts
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        feedItems.addAll(postItems);

        // 전시 조회
        Page<Event> events = eventRepository.findAll(pageRequest);

        List<FeedItemResponseDto> eventItems = events
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        feedItems.addAll(eventItems);

        // 아고라 조회
        Page<AgoraWithParticipant> agoras = agoraRepository.findAllAgoraAndParticipantByMember(member, pageRequest);
        List<FeedItemResponseDto> agoraItems = agoras
                .stream()
                .map(FeedItemResponseDto::from)
                .toList();
        feedItems.addAll(agoraItems);

        // application sort
        feedItems.sort((o1, o2) -> o2.getCreatedTime().compareTo(o1.getCreatedTime()));

        boolean hasNext = artworks.hasNext() || posts.hasNext() || events.hasNext() || agoras.hasNext();
        return FeedResponseDto.of(feedItems, hasNext);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDTO> getPostLikeRankWeek() {

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);
        List<PostResponseDTO> likePosts = postRepository.findTop10LikedPostByWeek(week)
                .stream()
                .map(PostResponseDTO::from)
                .limit(10)
                .toList();

        return likePosts;
    }

    @Transactional(readOnly = true)
    public List<ArtworkResponseDTO> getArtworkLikeRankWeek() {

        LocalDateTime week = LocalDateTime.now().minusWeeks(1);
        List<ArtworkResponseDTO> likePosts = artworkRepository.findTop10LikedArtworkByWeek(week)
                .stream()
                .map(ArtworkResponseDTO::from)
                .limit(10)
                .toList();

        return likePosts;
    }
}
