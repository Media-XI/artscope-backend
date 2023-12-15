package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.Event;
import com.example.codebase.domain.exhibition.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.startDate >= :startDate AND e.endDate <= :endDate ORDER BY e.startDate")
    Page<Event> findAllBySearchCondition(LocalDate startDate, LocalDate endDate, PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE e.startDate >= :startDate AND e.endDate <= :endDate AND e.type = :eventType ORDER BY e.startDate")
    Page<Event> findAllBySearchConditionAndEventType(LocalDate startDate, LocalDate endDate, EventType eventType, PageRequest pageRequest);

    @Query("SELECT e FROM Event e WHERE e.seq = :seq")
    Optional<Event> findBySeq(Long seq);
}
