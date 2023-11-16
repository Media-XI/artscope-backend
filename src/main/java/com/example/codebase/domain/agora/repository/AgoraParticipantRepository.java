package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.entity.AgoraParticipant;
import com.example.codebase.domain.agora.entity.AgoraParticipantIds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgoraParticipantRepository extends JpaRepository<AgoraParticipant, AgoraParticipantIds> {

}
