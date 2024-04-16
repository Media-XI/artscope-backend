package com.example.codebase.domain.member.dto;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.dto.TeamResponse;
import com.example.codebase.domain.team.entity.TeamUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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

    private List<TeamResponse.ProfileGet> teams = new ArrayList<>();

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
                dto.teams.add(TeamResponse.ProfileGet.from(teamUser.getTeam()));
            }
        }

        return dto;
    }

}
