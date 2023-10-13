package com.example.codebase.domain.artwork.entity;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkUpdateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "artwork")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate // 변경된 필드만 update 쿼리에 반영
public class Artwork {
    @Id
    @Column(name = "artwork_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "tags")
    private String tags;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "views", columnDefinition = "integer default 0")
    private Integer views = 0;

    @Builder.Default
    @Column(name = "likes", columnDefinition = "integer default 0")
    private Integer likes = 0;

    @Builder.Default
    @Column(name = "comments")
    private Integer comments = 0;

    @Column(name = "visible")
    private boolean visible;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL)
    private List<ArtworkMedia> artworkMedia = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL)
    private List<ArtworkLikeMember> artworkLikeMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL)
    private List<ArtworkComment> artworkComments = new ArrayList<>();

    public static Artwork of (ArtworkCreateDTO dto, Member member) {
        String tempTags = "";
        if (Optional.ofNullable(dto.getTags()).isPresent()) {
            tempTags = dto.getTags().stream()
                    .collect(Collectors.joining(","));
        }

        return Artwork.builder()
                .title(dto.getTitle())
                .tags(tempTags)
                .description(dto.getDescription())
                .visible(dto.getVisible())
                .createdTime(LocalDateTime.now())
                .member(member)
                .build();
    }

    public void addArtworkMedia(ArtworkMedia artworkMedia) {
        this.artworkMedia.add(artworkMedia);
    }

    public void addArtworkLikeMember(ArtworkLikeMember artworkLikeMember) {
        this.artworkLikeMembers.add(artworkLikeMember);
    }

    public void addArtworkComment(ArtworkComment artworkComment) {
        this.artworkComments.add(artworkComment);
        this.comments++;
    }

    public void update(ArtworkUpdateDTO dto) {
        this.title = dto.getTitle();
        if (Optional.ofNullable(dto.getTags()).isPresent()) {
            this.tags = dto.getTags().stream()
                    .collect(Collectors.joining(","));
        }
        this.description = dto.getDescription();
        this.visible = dto.isVisible();
        this.updatedTime = LocalDateTime.now();
    }

    public void updateArtworkMedia(Long mediaId, ArtworkMediaCreateDTO dto) {
        ArtworkMedia artworkMedia = this.artworkMedia.stream()
                .filter(media -> media.getId().equals(mediaId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 미디어 파일을 찾을 수 없습니다."));

        artworkMedia.update(dto);
    }

    public void increaseView() {
        this.views++;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }
}
