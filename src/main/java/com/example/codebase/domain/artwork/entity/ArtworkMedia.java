package com.example.codebase.domain.artwork.entity;

import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "artwork_media")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtworkMedia {

    @Id
    @Column(name = "artwork_media_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private ArtworkMediaType artworkMediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "image_width")
    private Integer imageWidth;

    @Column(name = "image_height")
    private Integer imageHeight;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    public static ArtworkMedia of (ArtworkMediaCreateDTO media, Artwork artwork) {
        return ArtworkMedia.builder()
                .artworkMediaType(ArtworkMediaType.create(media.getMediaType()))  // create() 메서드를 통해 MediaType을 생성 과 예외처리를 한다.
                .mediaUrl(media.getMediaUrl())
                .imageWidth(media.getWidth())
                .imageHeight(media.getHeight())
                .description(media.getDescription())
                .artwork(artwork)
                .createdTime(LocalDateTime.now())
                .build();
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public void update(ArtworkMediaCreateDTO media) {
        this.artworkMediaType = ArtworkMediaType.create(media.getMediaType());
        this.mediaUrl = media.getMediaUrl();
        this.description = media.getDescription();
        this.updatedTime = LocalDateTime.now();
    }
}
