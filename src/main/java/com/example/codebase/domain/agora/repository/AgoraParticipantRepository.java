package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import com.example.codebase.domain.agora.entity.AgoraParticipantIds;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AgoraParticipantRepository extends JpaRepository<AgoraParticipant, AgoraParticipantIds> {

}
