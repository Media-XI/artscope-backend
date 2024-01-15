package com.example.codebase.domain.event.repository;

import com.example.codebase.domain.event.entity.Event;
import com.example.codebase.domain.event.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.seq = :seq")
    Optional<Event> findBySeq(Long seq);

    @Query("SELECT e FROM Event e JOIN e.member m WHERE (:username IS NULL OR m.username = :username) AND e.startDate >= :startDate AND e.endDate <= :endDate"
            + " AND (:eventType IS NULL OR e.type = :eventType) ORDER BY e.startDate")
    Page<Event> findByOptionalUsernameAndEventType(String username, LocalDate startDate, LocalDate endDate, EventType eventType, PageRequest pageRequest);

}