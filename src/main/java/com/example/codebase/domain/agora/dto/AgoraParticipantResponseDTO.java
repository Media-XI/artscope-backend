package com.example.codebase.domain.agora.dto;


import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraOpinion;
import com.example.codebase.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgoraParticipantResponseDTO {

    private String name;

    private String username;

    private String profileImageUrl;

    public static AgoraParticipantResponseDTO of(Member member, Boolean isAnonymous, Integer agoraSequence) {
        // 익명 여부에 따른 이름 설정
        String authorName = member.getName();
        String authorUsername = member.getUsername();

        if (isAnonymous) {
            authorName = "익명 " + agoraSequence;
            authorUsername = null;
            // 0번이면 익명 작성자
            if (agoraSequence == 0) {
                authorName = "익명 작성자";
            }
        }

        AgoraParticipantResponseDTO agoraParticipantResponseDTO = new AgoraParticipantResponseDTO();
        agoraParticipantResponseDTO.setName(authorName);
        agoraParticipantResponseDTO.setUsername(authorUsername);
        agoraParticipantResponseDTO.setProfileImageUrl(member.getPicture());
        return agoraParticipantResponseDTO;
    }

    public static AgoraParticipantResponseDTO from(AgoraOpinion agoraOpinion) {
        Agora agora = agoraOpinion.getAgora();
        Member member = agoraOpinion.getMember();
        Boolean isAnonymous = agora.getIsAnonymous();
        Integer agoraSequence = agoraOpinion.getAuthorSequence();

        return AgoraParticipantResponseDTO.of(member, isAnonymous, agoraSequence);
    }
}
