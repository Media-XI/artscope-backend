package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraOpinion;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgoraDetailReponseDTO {

    private AgoraResponseDTO agora;

    private String userVoteStatus;

    // 찬성 의견
    private List<AgoraOpinionResponseDTO> agreeOpinions;

    // 중립 의견
    private List<AgoraOpinionResponseDTO> naturalOpinions;

    // 반대 의견
    private List<AgoraOpinionResponseDTO> disagreeOpinions;

    public static AgoraDetailReponseDTO from(Agora agora) {
        List<AgoraOpinionResponseDTO> agreeOpinionDTOs = agora.getOpinions().stream()
            .filter(opinion -> opinion.isSameVoteAndNotDeleted(agora.getAgreeText()))
            .map(AgoraOpinionResponseDTO::from)
            .collect(Collectors.toList());

        List<AgoraOpinionResponseDTO> naturalOpinionsDTOs = agora.getOpinions().stream()
            .filter(opinion -> opinion.isSameVoteAndNotDeleted(agora.getNaturalText()))
            .map(AgoraOpinionResponseDTO::from)
            .collect(Collectors.toList());

        List<AgoraOpinionResponseDTO> disagreeOpinionDTOs = agora.getOpinions().stream()
            .filter(opinion -> opinion.isSameVoteAndNotDeleted(agora.getDisagreeText()))
            .map(AgoraOpinionResponseDTO::from)
            .collect(Collectors.toList());

        AgoraResponseDTO agoraDTO = AgoraResponseDTO.from(agora);
        return of(agoraDTO, agreeOpinionDTOs, disagreeOpinionDTOs, naturalOpinionsDTOs);
    }

    public static AgoraDetailReponseDTO of(Agora agora, AgoraParticipant participant) {
        List<AgoraOpinionResponseDTO> agreeOpinionDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.isSameVoteAndNotDeleted(agora.getAgreeText()))
                .map((AgoraOpinion agoraOpinion) -> AgoraOpinionResponseDTO.of(agoraOpinion, participant))
                .collect(Collectors.toList());

        List<AgoraOpinionResponseDTO> naturalOpinionsDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.isSameVoteAndNotDeleted(agora.getNaturalText()))
                .map((AgoraOpinion agoraOpinion) -> AgoraOpinionResponseDTO.of(agoraOpinion, participant))
                .collect(Collectors.toList());

        List<AgoraOpinionResponseDTO> disagreeOpinionDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.isSameVoteAndNotDeleted(agora.getDisagreeText()))
                .map((AgoraOpinion agoraOpinion) -> AgoraOpinionResponseDTO.of(agoraOpinion, participant))
                .collect(Collectors.toList());

        AgoraResponseDTO agoraDTO = AgoraResponseDTO.of(agora, participant);

        AgoraDetailReponseDTO detailDto = of(agoraDTO, agreeOpinionDTOs, disagreeOpinionDTOs, naturalOpinionsDTOs);
        detailDto.setUserVoteStatus(participant.getVote());
        return detailDto;
    }

    private static AgoraDetailReponseDTO of(AgoraResponseDTO agora, List<AgoraOpinionResponseDTO> agreeOpinions, List<AgoraOpinionResponseDTO> disagreeOpinions, List<AgoraOpinionResponseDTO> naturalOpinionsDTOs) {
        return AgoraDetailReponseDTO.builder()
                .agora(agora)
                .agreeOpinions(agreeOpinions)
                .naturalOpinions(naturalOpinionsDTOs)
                .disagreeOpinions(disagreeOpinions)
                .userVoteStatus(null)
                .build();
    }
}
