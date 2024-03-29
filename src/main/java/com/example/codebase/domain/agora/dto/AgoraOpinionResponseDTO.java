package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.AgoraOpinion;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Builder.Default
    private Boolean isMine = false; // 내가 쓴 의견인지 여부

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    private AgoraParticipantResponseDTO author;

    public static AgoraOpinionResponseDTO from(AgoraOpinion opinion) {
        return AgoraOpinionResponseDTO.builder()
            .content(opinion.getContent())
            .vote(opinion.getAuthorVote())
            .createdTime(opinion.getCreatedTime())
            .updatedTime(opinion.getUpdatedTime())
            .author(AgoraParticipantResponseDTO.from(opinion))
            .build();
    }

    public static AgoraOpinionResponseDTO of(AgoraOpinion opinion, AgoraParticipant agoraParticipant) {
        AgoraOpinionResponseDTO dto = from(opinion);
        dto.isMine = opinion.isAuthor(agoraParticipant.getMemberUsername());
        return dto;
    }


}
