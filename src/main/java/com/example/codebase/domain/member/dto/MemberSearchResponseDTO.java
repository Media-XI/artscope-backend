package com.example.codebase.domain.member.dto;

import com.example.codebase.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberSearchResponseDTO {
    private String username;
    private String name;
    private String picture;
    private String companyName;
    private String companyRole;
    private String roleStatus;

    public static MemberSearchResponseDTO from(Member member) {
        return MemberSearchResponseDTO.builder()
            .username(member.getUsername())
            .name(member.getName())
            .picture(member.getPicture())
            .companyName(member.getCompanyName())
            .companyRole(member.getCompanyRole())
            .roleStatus(String.valueOf(member.getRoleStatus()))
            .build();
    }
}
