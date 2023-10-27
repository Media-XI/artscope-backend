package com.example.codebase.domain.artwork.entity;

import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.media.MediaType;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private MediaType artworkMediaType;

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

    public static ArtworkMedia of(ArtworkMediaCreateDTO media, Artwork artwork) {
        return ArtworkMedia.builder()
                .artworkMediaType(MediaType.create(media.getMediaType()))  // create() 메서드를 통해 MediaType을 생성 과 예외처리를 한다.
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
        this.artworkMediaType = MediaType.create(media.getMediaType());
        this.mediaUrl = media.getMediaUrl();
        this.description = media.getDescription();
        this.updatedTime = LocalDateTime.now();
    }
}
