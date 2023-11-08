package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.ExhibitionParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionParticipantRepository
        extends JpaRepository<ExhibitionParticipant, Long> {
}
