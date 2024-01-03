package com.example.codebase.domain.Event.repository;

import com.example.codebase.domain.Event.entity.ExhibitionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionParticipantRepository
    extends JpaRepository<ExhibitionParticipant, Long> {
}
