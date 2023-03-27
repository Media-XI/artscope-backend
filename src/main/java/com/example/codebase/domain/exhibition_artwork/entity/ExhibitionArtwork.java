package com.example.codebase.domain.exhibition_artwork.entity;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exhibition_artwork")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionArtwork {

    @Id
    @Column(name = "exhibition_artwork_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private Exhibition exhibition;

    @ManyToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExhibitionArtworkStatus status;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
