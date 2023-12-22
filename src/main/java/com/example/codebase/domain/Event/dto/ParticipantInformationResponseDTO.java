package com.example.codebase.domain.Event.dto;

import com.example.codebase.domain.Event.entity.ExhibitionParticipant;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInformationResponseDTO {

    private String username;

    private String name;

}
