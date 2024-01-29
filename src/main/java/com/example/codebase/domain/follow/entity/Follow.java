package com.example.codebase.domain.follow.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(FollowIds.class)
public class Follow {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_id", columnDefinition = "BINARY(16)")
    private Member follow;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", columnDefinition = "BINARY(16)")
    private Member follower;

    @Column(name = "follow_time")
    private LocalDateTime followTime;

    public static Follow of(Member follow, Member follower) {
        return Follow.builder()
                .follow(follow)
                .follower(follower)
                .followTime(LocalDateTime.now())
                .build();
    }

}
