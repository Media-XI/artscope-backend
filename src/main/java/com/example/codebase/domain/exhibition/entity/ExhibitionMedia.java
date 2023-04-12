package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.entity.ArtworkMediaType;
import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "exhibition_media")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExhibitionMedia {

    @Id
    @Column(name = "exhibition_media_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private ExhibtionMediaType exhibtionMediaType;

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "exhibition_id")
    private Exhibition exhibition;

    public static ExhibitionMedia of(ExhibitionMediaCreateDTO mediaCreateDTO, Exhibition exhibition) {
        return ExhibitionMedia.builder()
                .exhibtionMediaType(ExhibtionMediaType.create(mediaCreateDTO.getMediaType()))
                .mediaUrl(mediaCreateDTO.getMediaUrl())
                .exhibition(exhibition)
                .createdTime(LocalDateTime.now())
                .build();
    }
}
