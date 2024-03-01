package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.magazine.repository.MagazineMediaRepository;
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

    enum MagazineMediaType {
        IMAGE, VIDEO
    }

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
                .type(MagazineMediaType.IMAGE)
                .url(mediaUrl)
                .magazine(newMagazine)
                .build();
        newMagazine.addMedia(magazineMedia);
        return magazineMedia;
    }

    public static List<MagazineMedia> toList(List<String> mediaUrls, Magazine newMagazine) {
        return mediaUrls.stream()
                .map(mediaUrl -> MagazineMedia.toEntity(mediaUrl, newMagazine))
                .toList();
    }
}
