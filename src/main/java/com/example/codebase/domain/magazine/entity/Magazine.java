package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.magazine.dto.MagazineRequest;
import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "magazine")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted = false")
public class Magazine {

    @Id
    @Column(name = "magazine_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "metadata", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> metadata;

    @Builder.Default
    @Column(name = "views", columnDefinition = "integer default 0")
    private Integer views = 0;

    @Builder.Default
    @Column(name = "likes", columnDefinition = "integer default 0")
    private Integer likes = 0;

    @Builder.Default
    @Column(name = "comments", columnDefinition = "integer default 0")
    private Integer comments = 0;

    @Builder.Default
    @Column(name = "visibled")
    private Boolean visibled = true;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private MagazineCategory category;

    @Builder.Default
    @BatchSize(size = 25)
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL)
    private List<MagazineComment> magazineComments = new ArrayList<>();

    @Builder.Default
    @BatchSize(size = 25)
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL)
    private List<MagazineLike> magazineLikes = new ArrayList<>();

    @Builder.Default
    @BatchSize(size = 25)
    @OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL)
    private List<MagazineMedia> magazineMedias = new ArrayList<>();

    public static Magazine toEntity(MagazineRequest.Create magazineRequest, Member member, MagazineCategory category) {
        return Magazine.builder()
                .title(magazineRequest.getTitle())
                .content(magazineRequest.getContent())
                .metadata(magazineRequest.getMetadata())
                .member(member)
                .category(category)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public boolean isOwner(String loginUsername) {
        return member.getUsername().equals(loginUsername);
    }

    public void update(MagazineRequest.Update magazineRequest) {
        this.title = magazineRequest.getTitle();
        this.content = magazineRequest.getContent();
        this.metadata = magazineRequest.getMetadata();
        this.updatedTime = LocalDateTime.now();
    }

    public void incressView() {
        this.views++;
    }

    public void addComment(MagazineComment entity) {
        this.magazineComments.add(entity);
        this.comments = this.magazineComments.size();
    }

    public void delete() {
        this.isDeleted = true;
        this.updatedTime = LocalDateTime.now();
        // 양방향 매핑 제거
        this.magazineComments.clear();
    }

    public void addLike(MagazineLike magazineLike) {
        this.magazineLikes.add(magazineLike);
        this.likes = this.magazineLikes.size();
    }

    public void removeLike(MagazineLike magazineLike) {
        this.magazineLikes.remove(magazineLike);
        this.likes = this.magazineLikes.size();
    }

    public void addMedia(MagazineMedia magazineMedia) {
        this.magazineMedias.add(magazineMedia);
    }
}
