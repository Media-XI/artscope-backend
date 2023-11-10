package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

  @Query(
      "SELECT e "
          + "FROM Exhibition e INNER JOIN EventSchedule es ON es.exhibition = e "
          + "WHERE es.startTime >= :startLocalDateTime AND es.endTime <= :endLocalDateTime "
          + "AND e.enabled = true "
          + "ORDER BY es.startTime")
  Page<Exhibition> findExhibitionsWithEventSchedules(
      LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime, Pageable pageable);

  @Query(
      "SELECT e "
          + "FROM Exhibition e INNER JOIN EventSchedule es ON es.exhibition = e "
          + "WHERE es.startTime >= :startLocalDateTime AND es.endTime <= :endLocalDateTime "
          + "AND e.enabled = true AND e.type = :eventType  "
          + "ORDER BY es.startTime")
  Page<Exhibition> findExhibitionsWithEventSchedules(
      LocalDateTime startLocalDateTime,
      LocalDateTime endLocalDateTime,
      EventType eventType,
      Pageable pageable);
}
