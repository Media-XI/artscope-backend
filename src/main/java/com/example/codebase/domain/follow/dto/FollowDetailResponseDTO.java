package com.example.codebase.domain.follow.dto;

import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.RoleStatus;
import com.example.codebase.domain.team.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FollowDetailResponseDTO {

    private String id;
    private String name;
    private String profileImage;
    private String introduction;
    private String entityType;
    private String followStatus;

    public static FollowDetailResponseDTO of(Member member, String status){
        return FollowDetailResponseDTO.builder()
                .id(member.getId().toString())
                .name(member.getUsername())
                .profileImage(member.getPicture())
                .introduction(member.getIntroduction())
                .entityType("member")
                .followStatus(status)
                .build();
    }

    public static FollowDetailResponseDTO of(Team team, String status){
        String teamDescription = team.getDescription().length() > 25 ? team.getDescription().substring(0, 25) + "..." : team.getDescription();
        return FollowDetailResponseDTO.builder()
                .id(team.getId().toString())
                .name(team.getName())
                .profileImage(team.getProfileImage())
                .introduction(teamDescription)
                .entityType("team")
                .followStatus(status)
                .build();
    }

}
