package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionWithEventSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @Query(
            "SELECT e as exhibition, es as eventSchedule "
                    + "FROM Exhibition e INNER JOIN EventSchedule es ON es.exhibition = e "
                    + "WHERE es.startDateTime>= :startDate "
                    + "AND es.endDateTime <= :endDate "
                    + "AND e.enabled = true "
                    + "ORDER BY es.startDateTime")
    Page<ExhibitionWithEventSchedule> findExhibitionsWithEventSchedules(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query(
            "SELECT e as exhibition, es as eventSchedule "
                    + "FROM Exhibition e INNER JOIN EventSchedule es ON es.exhibition = e "
                    + "WHERE es.startDateTime >= :startDate "
                    + "AND es.endDateTime <= :endDate "
                    + "AND e.enabled = true AND e.type = :eventType  "
                    + "ORDER BY es.startDateTime")
    Page<ExhibitionWithEventSchedule> findExhibitionsWithEventSchedules(
            LocalDateTime startDate, LocalDateTime endDate,
            EventType eventType,
            Pageable pageable);

    @Query("SELECT e FROM Exhibition e WHERE e.seq = :seq AND e.enabled = true")
    Optional<Exhibition> findBySeq(Long seq);

    @Query(value = "SELECT * FROM exhibition", nativeQuery = true)
    List<Exhibition> findAllExhibitionsIgnoreEnabled();

}
