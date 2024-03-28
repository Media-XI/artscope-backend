package com.example.codebase.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "team")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = PROTECTED)
@SoftDelete(columnName = "is_deleted", strategy = SoftDeleteType.DELETED)
public class Team {

    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
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

}
