package com.example.codebase.domain.team.entity;

import com.example.codebase.domain.team.dto.TeamRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "team")
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@SoftDelete(columnName = "is_deleted", strategy = SoftDeleteType.DELETED)
public class Team {

    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address")
    private String address;

    @Column(name = "profile_image", nullable = false)
    private String profileImage;

    @Column(name = "background_image", nullable = false)
    private String backgroundImage;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_time")
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamUser> teamUsers = new ArrayList<>();

    public static Team toEntity(TeamRequest.Create request) {
        return Team.builder()
                .name(request.getName())
                .address(request.getAddress())
                .profileImage(request.getProfileImage())
                .backgroundImage(request.getBackgroundImage())
                .description(request.getDescription())
                .build();
    }

    public void update(TeamRequest.Update request) {
        this.name = request.getName();
        this.address = request.getAddress();
        this.profileImage = request.getProfileImage();
        this.backgroundImage = request.getBackgroundImage();
        this.description = request.getDescription();
        this.updatedTime = LocalDateTime.now();
    }

    public void addTeamUser(TeamUser teamUser) {
        this.teamUsers.add(teamUser);
    }
}
