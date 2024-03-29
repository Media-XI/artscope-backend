package com.example.codebase.domain.follow.dto;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.RoleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class FollowMemberDetailResponseDTO {

    private String userId;
    private String username;
    private String profileImage;
    private String introduction;
    private RoleStatus roleStatus;
    private String status;

    public static FollowMemberDetailResponseDTO of(Member member, String status){
        return FollowMemberDetailResponseDTO.builder()
                .userId(member.getId().toString())
                .username(member.getUsername())
                .profileImage(member.getPicture())
                .introduction(member.getIntroduction())
                .roleStatus(member.getRoleStatus())
                .status(status)
                .build();
    }
}
