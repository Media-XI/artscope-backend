package com.example.codebase.domain.curation.repository;

import com.example.codebase.domain.curation.dto.CurationTime;
import com.example.codebase.domain.curation.entity.Curation;
import com.example.codebase.domain.magazine.entity.Magazine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CurationRepository extends JpaRepository<Curation, Long> {
    @Query("SELECT c FROM Curation c WHERE c.magazine = :magazine")
    Optional<Curation> findByMagazine(Magazine magazine);

    @Query("SELECT c FROM Curation c LEFT JOIN Magazine m ON c.magazine = m WHERE c.updatedTime >= :startTime AND m.isDeleted = false")
    Page<Curation> findByUpdatedTimeAfter(LocalDateTime startTime, PageRequest pageRequest);

    default Page<Curation> findByUpdatedTimeAfter(CurationTime time, PageRequest pageRequest) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = switch (time) {
            case WEEK -> now.minusWeeks(1);
            case MONTH -> now.minusMonths(1);
        };

        return findByUpdatedTimeAfter(startTime, pageRequest);
    }
}


