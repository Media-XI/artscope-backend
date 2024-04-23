package com.example.codebase.domain.team.entity;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.dto.TeamUserRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "team_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"team_id", "member_id"})
        })
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class TeamUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "position", nullable = false)
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

    public static TeamUser toEntity(String position, Member member, Team team, TeamUserRole role) {
        TeamUser teamUser = TeamUser.builder()
                .member(member)
                .team(team)
                .role(role)
                .position(position)
                .build();
        member.addTeamUser(teamUser);
        return teamUser;
    }

    public void validOwner() {
        if (role != TeamUserRole.OWNER) {
            throw new IllegalArgumentException("팀장만 가능한 작업입니다.");
        }
    }

    public boolean isOwner() {
        return role == TeamUserRole.OWNER;
    }

    public void transferOwner(TeamUser transferUser) {
        this.role = TeamUserRole.MEMBER;
        transferUser.role = TeamUserRole.OWNER;
    }

    public void update(TeamUserRequest.Update request) {
        this.position = request.getPosition();
        this.updatedTime = LocalDateTime.now();
    }

}

