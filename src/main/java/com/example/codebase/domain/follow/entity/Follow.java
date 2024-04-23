package com.example.codebase.domain.follow.entity;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow",
        uniqueConstraints = {
                @UniqueConstraint(name = "UniqueFollowerAndAllFollowingDomains", columnNames = {
                        "follower_id", "following_member_id", "following_team_id"
                })
        })
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", columnDefinition = "BINARY(16)")
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_member_id", columnDefinition = "BINARY(16)")
    private Member followingMember;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "following_team_id")
    private Team followingTeam;

    @Column(name = "follow_time")
    @Builder.Default
    private LocalDateTime followTime = LocalDateTime.now();

    public static Follow of(Member follower, Member following) {
        return Follow.builder()
                .follower(follower)
                .followingMember(following)
                .build();
    }

    public static Follow of(Member follower, Team following) {
        return Follow.builder()
                .follower(follower)
                .followingTeam(following)
                .build();
    }

}
