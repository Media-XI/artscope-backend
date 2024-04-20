package com.example.codebase.domain.team.dto;

import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.entity.TeamUserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class TeamResponse {

    @Getter
    @Setter
    @Schema(name = "TeamResponse.Get", description = "팀 조회 DTO")
    public static class Get{

        private Long id;

        private String description;

        private String address;

        private String profileImage;

        private String backgroundImage;

        private String name;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedTime;

        public static TeamResponse.Get from(Team team) {
            Get get = new Get();
            get.setId(team.getId());
            get.setDescription(team.getDescription());
            get.setAddress(team.getAddress());
            get.setProfileImage(team.getProfileImage());
            get.setBackgroundImage(team.getBackgroundImage());
            get.setName(team.getName());
            get.setCreatedTime(team.getCreatedTime());
            get.setUpdatedTime(team.getUpdatedTime());
            return get;
        }
    }

    @Getter
    @Setter
    @Schema(name = "TeamResponse.ProfileGet", description = "팀 프로파일 조회 DTO")
    public static class ProfileGet{
        private Long id;

        private String profileImage;

        private String name;

        private TeamUserRole role;

        public static TeamResponse.ProfileGet from(TeamUser teamUser){
            ProfileGet profileGet = new ProfileGet();
            profileGet.setId(teamUser.getTeam().getId());
            profileGet.setProfileImage(teamUser.getTeam().getProfileImage());
            profileGet.setName(teamUser.getTeam().getName());
            profileGet.setRole(teamUser.getRole());
            return profileGet;
        }
    }
}
