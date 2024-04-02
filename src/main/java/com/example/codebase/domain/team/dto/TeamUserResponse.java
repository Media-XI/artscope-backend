package com.example.codebase.domain.team.dto;

import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.entity.TeamUserRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

public class TeamUserResponse {

    @Getter
    @Setter
    @NoArgsConstructor(access = PROTECTED)
    @Schema(name = "TeamUserResponse.Get", description = "팀 유저 조회 DTO")
    public static class Get{

        private String username;

        private String profileImage;

        private String position;

        private TeamUserRole role;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedTime;


        public static TeamUserResponse.Get from(TeamUser teamUser) {
            TeamUserResponse.Get get = new TeamUserResponse.Get();
            get.setUsername(teamUser.getMember().getUsername());
            get.setProfileImage(teamUser.getMember().getPicture());
            get.setPosition(teamUser.getPosition());
            get.setRole(teamUser.getRole());
            get.setCreatedTime(teamUser.getCreatedTime());
            get.setUpdatedTime(teamUser.getUpdatedTime());
            return get;
        }
    }

    @Getter
    @Setter
    @Schema(name = "TeamUserResponse.GetAll", description = "팀 유저 전체 조회 DTO")
    public static class GetAll{

        private List<Get> teamUsers;

        public static TeamUserResponse.GetAll from(List<TeamUser> teamUsers) {
            GetAll getAll = new GetAll();
             getAll.teamUsers = teamUsers.stream()
                    .map(TeamUserResponse.Get::from)
                    .toList();
            return getAll;
        }
    }
}
