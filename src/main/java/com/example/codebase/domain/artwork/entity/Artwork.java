package com.example.codebase.domain.artwork.entity;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artwork")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Artwork {
    @Id
    @Column(name = "artwork_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "visible")
    private boolean visible;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL)
    private List<ArtworkMedia> artworkMedia;

    public static Artwork of (ArtworkCreateDTO dto, Member member) {
        return Artwork.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .visible(dto.isVisible())
                .createdTime(LocalDateTime.now())
                .member(member)
                .build();
    }

    public void addArtworkMedia(ArtworkMedia artworkMedia) {
        if (this.artworkMedia.size() >= 5) {
            throw new RuntimeException("미디어 파일 개수는 5개까지만 등록할 수 있습니다.");
        }
        this.artworkMedia.add(artworkMedia);
    }
}
