package com.example.codebase.domain.exhibition.entity;

import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
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
import lombok.Setter;

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
