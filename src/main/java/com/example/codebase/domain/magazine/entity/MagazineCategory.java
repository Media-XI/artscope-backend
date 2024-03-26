package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.magazine.dto.MagazineCategoryRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "magazine_category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "parent_id"})
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Where(clause = "is_deleted = false")
public class MagazineCategory {

    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Builder.Default
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private MagazineCategory parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<MagazineCategory> children = new ArrayList<>();

    public static MagazineCategory toEntity(String name, String slug, MagazineCategory parent) {
        MagazineCategory magazineCategory = MagazineCategory.builder()
                .name(name)
                .slug(slug)
                .parent(parent)
                .build();

        if(parent != null)
            parent.getChildren().add(magazineCategory);

        return magazineCategory;
    }

    public void delete() {
        if (!this.getChildren().isEmpty()) {
            throw new RuntimeException("하위 카테고리가 존재합니다.");
        }

        this.isDeleted = true;
        this.updatedTime = LocalDateTime.now();
    }

    public void checkDepth() {
        int depth = 0;
        MagazineCategory parent = this.parent;
        while (parent != null) {
            depth++;
            parent = parent.getParent();
        }

        if (depth >= 2) {
            throw new RuntimeException("카테고리는 최대 2단계 까지만 생성 가능합니다.");
        }
    }

    public void update(MagazineCategoryRequest.Update request) {
        Optional.ofNullable(request.getName())
                .ifPresent(name -> this.name = name);

        Optional.ofNullable(request.getSlug())
                .ifPresent(slug -> this.slug = slug);

        this.updatedTime = LocalDateTime.now();
    }

    public void changeParentCategory(MagazineCategory newParent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }

        this.parent = newParent;
        if (newParent != null) {
            newParent.getChildren().add(this);
        }
    }
}
