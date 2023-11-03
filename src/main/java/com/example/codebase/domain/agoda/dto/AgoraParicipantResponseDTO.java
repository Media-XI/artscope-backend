package com.example.codebase.domain.agoda.dto;


import com.example.codebase.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgoraParicipantResponseDTO {

    private String name;

    public static AgoraParicipantResponseDTO from(Member member, Boolean isAnonymous, Integer agoraSequence) {
        // 익명 여부에 따른 이름 설정
        String authorName = member.getName();

        if (isAnonymous && agoraSequence == 0) {
            authorName = "익명의 작성자";
        } else if (isAnonymous && agoraSequence > 0) {
            authorName = "익명 " + agoraSequence;
        }

        AgoraParicipantResponseDTO agoraParicipantResponseDTO = new AgoraParicipantResponseDTO();
        agoraParicipantResponseDTO.setName(authorName);
        return agoraParicipantResponseDTO;
    }

}
