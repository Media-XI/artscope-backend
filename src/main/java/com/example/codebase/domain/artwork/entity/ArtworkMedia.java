package com.example.codebase.domain.artwork.entity;

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

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "created_time")
    private LocalDateTime createdTime;
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;
}
