package com.example.codebase.domain.member.dto;

import com.example.codebase.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberSearchResponseDTO {
  private String username;
  private String picture;
  private String companyName;

  public static MemberSearchResponseDTO from(Member member) {
    return MemberSearchResponseDTO.builder()
        .username(member.getUsername())
        .picture(member.getPicture())
        .companyName(member.getCompanyName())
        .build();
  }
}
