package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraOpinion;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgoraOpinionRepository extends JpaRepository<AgoraOpinion, Long> {

}
