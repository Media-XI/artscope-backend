package com.example.codebase.domain.agora.dto;


import com.example.codebase.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgoraParticipantResponseDTO {

    private String name;

    public static AgoraParticipantResponseDTO from(Member member, Boolean isAnonymous, Integer agoraSequence) {
        // 익명 여부에 따른 이름 설정
        String authorName = member.getName();

        if (isAnonymous && agoraSequence == 0) {
            authorName = "익명의 작성자";
        } else if (isAnonymous && agoraSequence > 0) {
            authorName = "익명 " + agoraSequence;
        }

        AgoraParticipantResponseDTO agoraParticipantResponseDTO = new AgoraParticipantResponseDTO();
        agoraParticipantResponseDTO.setName(authorName);
        return agoraParticipantResponseDTO;
    }

}
