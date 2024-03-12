package com.example.codebase.domain.event.dto;

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
