package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AgoraParticipantIds implements Serializable {

    private Long agora;

    private UUID member;

    public static AgoraParticipantIds of(Agora agora, Member member) {
        return new AgoraParticipantIds(agora.getId(), member.getId());
    }

}
