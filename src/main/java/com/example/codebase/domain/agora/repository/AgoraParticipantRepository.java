package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import com.example.codebase.domain.agora.entity.AgoraParticipantIds;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgoraParticipantRepository extends JpaRepository<AgoraParticipant, AgoraParticipantIds> {

    Long countByAgora(Agora agora);

    Long countByAgoraAndVote(Agora agora, String vote);

    Optional<AgoraParticipant> findByMemberAndAgora(Member member, Agora agora);

    boolean existsByMemberAndAgora(Member member, Agora agora);
}
