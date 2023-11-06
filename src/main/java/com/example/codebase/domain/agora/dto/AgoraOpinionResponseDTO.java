package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.AgoraOpinion;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgoraOpinionResponseDTO {

    private String content;

    private String vote;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private AgoraParticipantResponseDTO author;

    public static AgoraOpinionResponseDTO from(AgoraOpinion opinion) {
        return AgoraOpinionResponseDTO.builder()
                .content(opinion.getContent())
                .vote(opinion.getAuthor().getVote())
                .createdTime(opinion.getCreatedTime())
                .updatedTime(opinion.getUpdatedTime())
                .author(AgoraParticipantResponseDTO.from(opinion))
                .build();
    }

}
