package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

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


}
