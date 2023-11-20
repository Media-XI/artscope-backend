package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.ExhibitionParticipant;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInformationResponseDTO {

    private String username;

    private String name;

    public static ParticipantInformationResponseDTO from(
            ExhibitionParticipant exhibitionParticipant) {
        String username = null;
        if (exhibitionParticipant.getMember() != null) {
            username = exhibitionParticipant.getMember().getUsername();
        }

        String name = exhibitionParticipant.getName();

        return ParticipantInformationResponseDTO.builder()
                .username(username)
                .name(name)
                .build();
    }
}
