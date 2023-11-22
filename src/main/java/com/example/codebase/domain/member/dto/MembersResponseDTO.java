package com.example.codebase.domain.member.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;

import java.util.List;

@Getter
public class MembersResponseDTO {

    List<MemberResponseDTO> members;

    PageInfo pageInfo;

    public static MembersResponseDTO of (List<MemberResponseDTO> members, PageInfo pageInfo) {
        MembersResponseDTO dto = new MembersResponseDTO();
        dto.members = members;
        dto.pageInfo = pageInfo;
        return dto;
    }
}
