package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.magazine.repository.MagazineMediaRepository;
import com.example.codebase.domain.media.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "magazine_media")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MagazineMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_media_id")
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MagazineMediaType type;

    @Column(name = "url")
    private String url;

    @Builder.Default
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    public static MagazineMedia toEntity(String mediaUrl, Magazine newMagazine) {
        MagazineMedia magazineMedia = MagazineMedia.builder()
                .type(MagazineMediaType.url)
                .url(mediaUrl)
                .build();
        magazineMedia.setMagaizne(newMagazine);
        return magazineMedia;
    }

    private void setMagaizne(Magazine magazine) {
        if (this.magazine != null) {
            this.magazine.getMagazineMedias().remove(this);
        }
        this.magazine = magazine;
        magazine.addMedia(this);
    }

    public static List<MagazineMedia> toList(List<String> mediaUrls, Magazine newMagazine) {
        return mediaUrls.stream()
                .map(mediaUrl -> MagazineMedia.toEntity(mediaUrl, newMagazine))
                .toList();
    }
}
