package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.exhibition.entity.EventType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {

    @Query(
            "SELECT es FROM EventSchedule es "
                    + "INNER JOIN Exhibition e ON es.exhibition = e"
                    + " WHERE es.eventDate BETWEEN :startDate AND :endDate"
                    + " AND e.type = :eventType"
                    + " AND e.enabled = true ")
    Page<EventSchedule> findByStartAndEndDate(
            LocalDateTime startDate, LocalDateTime endDate, EventType eventType, Pageable pageable);

    @Query(
            "SELECT es FROM EventSchedule es "
                    + "INNER JOIN Exhibition e ON es.exhibition = e"
                    + " WHERE es.eventDate BETWEEN :startDate AND :endDate"
                    + " AND e.enabled = true ")
    Page<EventSchedule> findByStartAndEndDate(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
