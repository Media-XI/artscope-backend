package com.example.codebase.domain.agora.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgoraDetailReponseDTO {

    private AgoraReponseDTO agora;

    // 찬성 의견
    private List<AgoraOpinionResponseDTO> agreeOpinions;

    // 반대 의견
    private List<AgoraOpinionResponseDTO> disagreeOpinions;

    public static AgoraDetailReponseDTO of(AgoraReponseDTO agora, List<AgoraOpinionResponseDTO> agreeOpinions, List<AgoraOpinionResponseDTO> disagreeOpinions) {
        return AgoraDetailReponseDTO.builder()
                .agora(agora)
                .agreeOpinions(agreeOpinions)
                .disagreeOpinions(disagreeOpinions)
                .build();
    }
}
