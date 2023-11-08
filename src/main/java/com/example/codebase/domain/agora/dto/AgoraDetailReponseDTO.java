package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.Agora;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgoraDetailReponseDTO {

    private AgoraReponseDTO agora;

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

        AgoraReponseDTO agoraDTO = AgoraReponseDTO.from(agora);
        return of(agoraDTO, agreeOpinionDTOs, disagreeOpinionDTOs, naturalOpinionsDTOs);
    }

    private static AgoraDetailReponseDTO of(AgoraReponseDTO agora, List<AgoraOpinionResponseDTO> agreeOpinions, List<AgoraOpinionResponseDTO> disagreeOpinions, List<AgoraOpinionResponseDTO> naturalOpinionsDTOs) {
        return AgoraDetailReponseDTO.builder()
                .agora(agora)
                .agreeOpinions(agreeOpinions)
                .naturalOpinions(naturalOpinionsDTOs)
                .disagreeOpinions(disagreeOpinions)
                .build();
    }

}
