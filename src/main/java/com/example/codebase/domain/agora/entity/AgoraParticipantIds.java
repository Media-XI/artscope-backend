package com.example.codebase.domain.agora.entity;

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


}
