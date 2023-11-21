package com.example.codebase.domain.agora.entity;

import java.util.Optional;

public interface AgoraWithParticipant {
    Agora getAgora();

    Optional<AgoraParticipant> getAgoraParticipant();

}
