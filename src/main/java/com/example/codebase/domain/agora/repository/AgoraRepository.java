package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraWithParticipant;
import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AgoraRepository extends JpaRepository<Agora, Long> {

    @Query("select a from Agora a where a.id = :id and a.isDeleted = false")
    Optional<Agora> findById(Long id);

    @Query("SELECT a AS agora, ap AS agoraParticipant " +
            "FROM Agora a LEFT JOIN AgoraParticipant ap ON (a = ap.agora AND ap.member = :member) " +
            "WHERE a.isDeleted = false")
    Page<AgoraWithParticipant> findAllAgoraAndParticipantByMember(Member member, Pageable pageable);
}