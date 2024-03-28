package com.example.codebase.domain.team.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "team_user")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "position")
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private TeamUserRole role;

    @Builder.Default
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
}
