package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionWithEventSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @Query(
            "SELECT e as exhibition, es as eventSchedule "
                    + "FROM Exhibition e INNER JOIN EventSchedule es ON es.exhibition = e "
                    + "WHERE es.eventDate BETWEEN :startDate AND :endDate "
                    + "AND e.enabled = true "
                    + "ORDER BY es.eventDate, es.startTime")
    Page<ExhibitionWithEventSchedule> findExhibitionsWithEventSchedules(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query(
            "SELECT e as exhibition, es as eventSchedule "
                    + "FROM Exhibition e INNER JOIN EventSchedule es ON es.exhibition = e "
                    + "WHERE es.eventDate BETWEEN :startDate AND :endDate "
                    + "AND e.enabled = true AND e.type = :eventType  "
                    + "ORDER BY es.eventDate, es.startTime")
    Page<ExhibitionWithEventSchedule> findExhibitionsWithEventSchedules(
            LocalDate startDate, LocalDate endDate,
            EventType eventType,
            Pageable pageable);
}
