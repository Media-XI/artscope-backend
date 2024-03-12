package com.example.codebase.domain.event.repository;

import com.example.codebase.domain.event.entity.ExhibitionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionParticipantRepository
    extends JpaRepository<ExhibitionParticipant, Long> {
}
