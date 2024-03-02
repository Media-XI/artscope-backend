package com.example.codebase.domain.magazine.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "magazine_like")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MagazineLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "magazine_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "magazine_id")
    private Magazine magazine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    @Builder.Default
    @Column(name = "liked_time")
    private LocalDateTime likedTime = LocalDateTime.now();
    
    public static MagazineLike toEntity(Magazine magazine, Member member) {
        return MagazineLike.builder()
                .magazine(magazine)
                .member(member)
                .build();
    }
}
