package com.example.codebase.domain.magazine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "magazine_category")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedTime = LocalDateTime.now();

    public static MagazineCategory toEntity(String name) {
        return MagazineCategory.builder()
                .name(name)
                .build();
    }
}
