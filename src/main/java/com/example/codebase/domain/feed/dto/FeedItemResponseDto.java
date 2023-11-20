package com.example.codebase.domain.feed.dto;


import com.example.codebase.domain.agora.dto.AgoraParticipantResponseDTO;
import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraMedia;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.entity.PostMedia;
import com.example.codebase.domain.post.entity.PostWithIsLiked;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    private String authorIntroduction;

    private String authorProfileImageUrl;

    private String authorCompanyName;

    private String authorCompanyRole;

    private FeedItemEventResponseDto event;

    private List<String> tags;

    private String categoryId;

    private Integer views;

    private Integer likes;

    private Integer comments;

    // Agora Option
    private Integer agoraAgreeCount;

    private Integer agoraDisagreeCount;

    private Integer agoraNaturalCount;

    private Integer participantCount;

    private String agreeText;

    private String disagreeText;

    private String naturalText;
    //

    @Builder.Default
    private Boolean isLiked = false;

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
        String authorUsername = artwork.getMember().getUsername();
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
            .authorIntroduction(authorDescription)
            .authorProfileImageUrl(authorProfileImageUrl)
            .authorCompanyName(
                artwork.getMember().getCompanyName() != null ? artwork.getMember().getCompanyName() : null)
            .authorCompanyRole(
                artwork.getMember().getCompanyRole() != null ? artwork.getMember().getCompanyRole() : null)
            .tags(tags)
            .categoryId(FeedType.artwork.name())
            .views(artwork.getViews())
            .likes(artwork.getLikes())
            .comments(artwork.getComments())
            .mediaUrls(mediaUrls)
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
        List<String> mediaUrls = post.getPostMedias().stream()
            .map(PostMedia::getMediaUrl)
            .collect(Collectors.toList());

        FeedItemResponseDto dto = FeedItemResponseDto.builder()
            .id(post.getId())
            .type(FeedType.post)
            .title(null)
            .content(post.getContent())
            .authorName(post.getAuthor().getName())
            .authorUsername(post.getAuthor().getUsername())
            .authorIntroduction(post.getAuthor().getIntroduction())
            .authorProfileImageUrl(post.getAuthor().getPicture())
            .authorCompanyName(
                post.getAuthor().getCompanyName() != null ? post.getAuthor().getCompanyName() : null)
            .authorCompanyRole(
                post.getAuthor().getCompanyRole() != null ? post.getAuthor().getCompanyRole() : null)
            .tags(null)
            .categoryId(FeedType.post.name())
            .views(post.getViews())
            .likes(post.getLikes())
            .comments(post.getComments())
            .mediaUrls(mediaUrls)
            .createdTime(post.getCreatedTime())
            .updatedTime(post.getUpdatedTime())
            .build();
        return dto;
    }

    public static FeedItemResponseDto from(PostWithIsLiked postWithIsLiked) {
        FeedItemResponseDto dto = from(postWithIsLiked.getPost());
        dto.setIsLiked(postWithIsLiked.getIsLiked());
        return dto;
    }

    public static FeedItemResponseDto from(Exhibition exhibition) {
        FeedItemResponseDto dto =
            FeedItemResponseDto.builder()
                .id(exhibition.getId())
                .type(FeedType.exhibition)
                .title(exhibition.getTitle())
                .content(exhibition.getDescription())
                .authorName(exhibition.getMember().getName())
                .authorUsername(exhibition.getMember().getUsername())
                .authorIntroduction(exhibition.getMember().getIntroduction())
                .authorProfileImageUrl(exhibition.getMember().getPicture())
                .authorCompanyName(
                    exhibition.getMember().getCompanyName() != null
                        ? exhibition.getMember().getCompanyName()
                        : null)
                .authorCompanyRole(
                    exhibition.getMember().getCompanyRole() != null
                        ? exhibition.getMember().getCompanyRole()
                        : null)
                .tags(null)
                .categoryId(FeedType.exhibition.name())
                .views(0)
                .likes(0)
                .comments(0)
                .createdTime(exhibition.getCreatedTime())
                .updatedTime(exhibition.getUpdatedTime())
                .build();

        if (exhibition.getFirstEventSchedule() != null) {
            FeedItemEventResponseDto eventDto =
                    FeedItemEventResponseDto.builder()
                            .eventType(exhibition.getType())
                            .startDateTime(exhibition.getFirstEventSchedule().getStartDateTime())
                            .endDateTime(exhibition.getFirstEventSchedule().getEndDateTime())
                            .locationName(exhibition.getFirstEventSchedule().getLocation().getName())
                            .locationAddress(exhibition.getFirstEventSchedule().getLocation().getAddress())
                            .detailLocation(exhibition.getFirstEventSchedule().getDetailLocation())
                            .build();
            dto.setEvent(eventDto);
        }

        if (exhibition.getExhibitionMedias().size() > 0) {
            String thumbnailUrl = exhibition.getExhibitionMedias().get(0).getMediaUrl();
            List<String> mediaUrls =
                exhibition.getExhibitionMedias().stream()
                    .map(ExhibitionMedia::getMediaUrl)
                    .collect(Collectors.toList());
            dto.setThumbnailUrl(thumbnailUrl);
            dto.setMediaUrls(mediaUrls);
        }

        return dto;
    }

    public static FeedItemResponseDto from(ArtworkWithIsLike artworkWithIsLike) {
        FeedItemResponseDto dto = from(artworkWithIsLike.getArtwork());
        dto.setIsLiked(artworkWithIsLike.getIsLike());
        return dto;
    }
    public static FeedItemResponseDto from(Agora agora) {
        AgoraParticipantResponseDTO agoraParticipantResponseDTO = AgoraParticipantResponseDTO.of(agora.getAuthor(),
                agora.getIsAnonymous(), 0);

        FeedItemResponseDto dto = FeedItemResponseDto.builder()
                .id(agora.getId())
                .type(FeedType.agora)
                .title(agora.getTitle())
                .content(agora.getContent())
                .authorName(agoraParticipantResponseDTO.getName())
                .authorUsername(agoraParticipantResponseDTO.getUsername())
                .authorProfileImageUrl(agoraParticipantResponseDTO.getProfileImageUrl())
                .tags(null)
                .categoryId(FeedType.agora.name())
                .views(0)
                .likes(0)
                .comments(0)
                .agoraAgreeCount(agora.getAgreeCount())
                .agoraDisagreeCount(agora.getDisagreeCount())
                .agoraNaturalCount(agora.getNaturalCount())
                .participantCount(agora.getParticipantCount())
                .agreeText(agora.getAgreeText())
                .disagreeText(agora.getDisagreeText())
                .naturalText(agora.getNaturalText())
                .createdTime(agora.getCreatedTime())
                .updatedTime(agora.getUpdatedTime())
                .build();

        if (agora.getMedias() != null && agora.getMedias().size() > 0) {
            String thumbnailUrl = agora.getMedias().stream().findFirst().get().getMediaUrl();
            List<String> mediaUrls = agora.getMedias().stream()
                    .map(AgoraMedia::getMediaUrl)
                    .collect(Collectors.toList());

            dto.setThumbnailUrl(thumbnailUrl);
            dto.setMediaUrls(mediaUrls);
        }

        return dto;
    }
}
