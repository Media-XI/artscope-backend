package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.ExhibitionParticipant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseParticipantInformationDTO {

  private String username;

  private String name;

  // TODO : 뭐가 더 필요하지..

  public static ResponseParticipantInformationDTO from(
      ExhibitionParticipant exhibitionParticipant) {
    return ResponseParticipantInformationDTO.builder()
        .username(exhibitionParticipant.getMember().getUsername())
        .name(exhibitionParticipant.getMember().getName())
        .build();
  }
}
