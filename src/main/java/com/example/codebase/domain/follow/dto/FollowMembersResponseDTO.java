package com.example.codebase.domain.follow.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowMembersResponseDTO {

    private List<FollowMemberDetailResponseDTO> followingList;

    private PageInfo pageInfo;

    public static FollowMembersResponseDTO of(List<FollowMemberDetailResponseDTO> followMemberDetailResponseDTO, PageInfo pageInfo ) {
        return FollowMembersResponseDTO.builder()
                .followingList(followMemberDetailResponseDTO)
                .pageInfo(pageInfo)
                .build();
    }
}
