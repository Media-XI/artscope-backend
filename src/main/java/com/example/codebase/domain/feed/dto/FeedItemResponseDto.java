package com.example.codebase.domain.feed.dto;


import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedItemResponseDto {
    private Long id;

    private String title;

    private String content;

    private FeedType type;

    private String thumbnailUrl;

    private List<String> mediaUrls;

    private String authorUsername;

    private String authorName;

    private String authorDescription;

    private String authorProfileImageUrl;

    private List<String> tags;

    private String categoryId;

    private Integer views;

    private Integer likes;

    @Builder.Default
    private Boolean isLiked = false;

    private Integer comments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;
    public static FeedItemResponseDto from(Artwork artwork) {
        String thumbnailUrl = artwork.getArtworkMedia().get(0).getMediaUrl();
        List<String> mediaUrls = artwork.getArtworkMedia().stream()
                .map(ArtworkMedia::getMediaUrl)
                .collect(Collectors.toList());
        String authorName = artwork.getMember().getName();
        String authorUsername = artwork.getMember().getUsername().toString();
        String authorDescription = artwork.getMember().getIntroduction();
        String authorProfileImageUrl = artwork.getMember().getPicture();
        List<String> tags = Arrays.stream(artwork.getTags().split(","))
                .map(String::trim).collect(Collectors.toList());

        FeedItemResponseDto dto = FeedItemResponseDto.builder()
                .id(artwork.getId())
                .type(FeedType.artwork)
                .title(artwork.getTitle())
                .content(artwork.getDescription())
                .thumbnailUrl(thumbnailUrl)
                .authorName(authorName)
                .authorUsername(authorUsername)
                .authorDescription(authorDescription)
                .authorProfileImageUrl(authorProfileImageUrl)
                .tags(tags)
                .categoryId(FeedType.artwork.name())
                .views(artwork.getViews())
                .likes(artwork.getLikes())
                .mediaUrls(mediaUrls)
                .comments(0)
                .createdTime(artwork.getCreatedTime())
                .updatedTime(artwork.getUpdatedTime())
                .build();
        return dto;
    }

    public static FeedItemResponseDto of(Artwork artwork, Boolean isLiked) {
        FeedItemResponseDto dto = from(artwork);
        dto.setIsLiked(isLiked);
        return dto;
    }

    public static FeedItemResponseDto from(Post post) {
        FeedItemResponseDto dto = FeedItemResponseDto.builder()
                .id(post.getId())
                .type(FeedType.post)
                .title(null)
                .content(post.getContent())
                .authorName(post.getAuthor().getName())
                .authorUsername(post.getAuthor().getUsername().toString())
                .authorDescription(post.getAuthor().getIntroduction())
                .authorProfileImageUrl(post.getAuthor().getPicture())
                .tags(null)
                .categoryId(FeedType.post.name())
                .views(post.getViews())
                .likes(post.getLikes())
                .mediaUrls(null)
                .comments(0)
                .createdTime(post.getCreatedTime())
                .updatedTime(post.getUpdatedTime())
                .build();
        return dto;
    }

    public static FeedItemResponseDto from (PostWithIsLiked postWithIsLiked) {
        FeedItemResponseDto dto = from(postWithIsLiked.getPost());
        dto.setIsLiked(postWithIsLiked.getIsLiked());
        return dto;
    }

    public static FeedItemResponseDto from(Exhibition exhibition) {
        FeedItemResponseDto dto = FeedItemResponseDto.builder()
                .id(exhibition.getId())
                .type(FeedType.exhibition)
                .title(exhibition.getTitle())
                .content(exhibition.getDescription())
                .authorName(exhibition.getMember().getName())
                .authorUsername(exhibition.getMember().getUsername())
                .authorDescription(exhibition.getMember().getIntroduction())
                .authorProfileImageUrl(exhibition.getMember().getPicture())
                .tags(null)
                .categoryId(FeedType.exhibition.name())
                .views(0)
                .likes(0)
                .mediaUrls(null)
                .comments(0)
                .createdTime(exhibition.getCreatedTime())
                .updatedTime(exhibition.getUpdatedTime())
                .build();
        return dto;
    }

    public static FeedItemResponseDto from(ArtworkWithIsLike artworkWithIsLike) {
        FeedItemResponseDto dto = from(artworkWithIsLike.getArtwork());
        dto.setIsLiked(artworkWithIsLike.getIsLike());
        return dto;
    }
}
