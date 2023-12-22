package com.example.codebase.domain.member.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.data.domain.Page;

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

    public static MembersResponseDTO from(Page<Member> members) {
        PageInfo pageInfo = PageInfo.from(members);
        List<MemberResponseDTO> dtos = members.stream()
                .map(MemberResponseDTO::from)
                .toList();
        return MembersResponseDTO.of(dtos, pageInfo);
    }
}
