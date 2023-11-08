package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.ExhibitionParticipant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInformationResponseDTO {

  private String username;

  private String name;

  // TODO : 뭐가 더 필요하지..

  public static ParticipantInformationResponseDTO from(
      ExhibitionParticipant exhibitionParticipant) {
    return ParticipantInformationResponseDTO.builder()
        .username(exhibitionParticipant.getMember().getUsername())
        .name(exhibitionParticipant.getName())
        .build();
  }
}
