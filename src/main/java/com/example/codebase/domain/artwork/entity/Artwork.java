package com.example.codebase.domain.artwork.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "artwork", cascade = CascadeType.ALL)
    private List<ArtworkMedia> artworkMedia;
}
