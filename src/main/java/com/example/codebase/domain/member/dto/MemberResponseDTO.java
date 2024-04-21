package com.example.codebase.domain.member.dto;

import com.example.codebase.domain.magazine.dto.MagazineCategoryResponse;
import com.example.codebase.domain.magazine.entity.MagazineCategory;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.entity.TeamUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class MemberResponseDTO {

    private String username;

    private String name;

    private String email;

    private String picture;

    private String oauthProvider;

    private boolean activated;

    private String roleStatus;

    private String snsUrl;

    private String websiteUrl;

    private String introduction;

    private String history;

    private String companyName;

    private String companyRole;

    private Boolean allowEmailReceive;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime allowEmailReceiveDateTime;

//    private Set<String> authrities;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;
    //
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    private List<TeamProfile> teams = new ArrayList<>();

    public static MemberResponseDTO from(Member member) {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setUsername(member.getUsername());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setPicture(member.getPicture());
        dto.setOauthProvider(String.valueOf(member.getOauthProvider()));
        // dto.setOauthProviderId(Optional.ofNullable(member.getOauthProviderId()));
        dto.setActivated(member.isActivated());
        /* dto.setAuthrities(
                member.getAuthorities().stream()
                        .map(authority -> authority.getAuthority().getAuthorityName()
                        )
                        .collect(Collectors.toSet())
        ); */
        dto.setCreatedTime(member.getCreatedTime());
        dto.setUpdatedTime(member.getUpdatedTime());
        dto.setRoleStatus(member.getRoleStatus().toString());

        // TODO : 일반유저는 NPE 발생 가능성 있음 해결하기
        dto.setSnsUrl(member.getSnsUrl() != null ? member.getSnsUrl() : null);
        dto.setWebsiteUrl(member.getWebsiteUrl() != null ? member.getWebsiteUrl() : null);
        dto.setIntroduction(member.getIntroduction() != null ? member.getIntroduction() : null);
        dto.setHistory(member.getHistory() != null ? member.getHistory() : null);

        // TODO : 기획자가 아니면 NPE 발생 가능성 있음 해결하기
        dto.setCompanyName(
                member.getCompanyName() != null ? member.getCompanyName() : null);
        dto.setCompanyRole(member.getCompanyRole() != null ? member.getCompanyRole() : null);

        dto.setAllowEmailReceive(member.isAllowEmailReceive());
        dto.setAllowEmailReceiveDateTime(member.getAllowEmailReceiveDatetime());

        if (!member.getTeamUser().isEmpty()) {
            for (TeamUser teamUser : member.getTeamUser()) {
                dto.getTeams().add(TeamProfile.from(teamUser));
            }
        }

        return dto;
    }

    @Getter
    @Setter
    public static class TeamProfile {

        private Long id;

        private String profileImage;

        private String name;

        public static TeamProfile from(TeamUser teamUser) {
            TeamProfile teamProfile = new TeamProfile();
            teamProfile.setId(teamUser.getTeam().getId());
            teamProfile.setName(teamUser.getTeam().getName());
            teamProfile.setProfileImage(teamUser.getTeam().getProfileImage());
            return teamProfile;
        }
    }

    @Getter
    @Setter
    public static class TeamProfileWithRole extends TeamProfile {
        private String role;

        public static TeamProfileWithRole from(TeamUser teamUser) {
            TeamProfileWithRole profileWithRole = new TeamProfileWithRole();
            profileWithRole.setId(teamUser.getTeam().getId());
            profileWithRole.setName(teamUser.getTeam().getName());
            profileWithRole.setProfileImage(teamUser.getTeam().getProfileImage());
            profileWithRole.setRole(teamUser.getRole().toString());
            return profileWithRole;
        }
    }

    @Getter
    @Setter
    @Schema(name = "MemberResponseDTO.TeamProfiles", description = "해당 사용자가 소속된 모든 팀과 권한 조회 DTO")
    public static class TeamProfiles {
        private List<TeamProfileWithRole> profiles;

        public static TeamProfiles from(List<TeamUser> teamUsers) {
            TeamProfiles teamProfiles = new TeamProfiles();
            teamProfiles.setProfiles(teamUsers.stream()
                    .map(TeamProfileWithRole::from)
                    .collect(Collectors.toList()));
            return teamProfiles;
        }
    }

}
