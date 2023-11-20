package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.exhibition.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface EventScheduleRepository extends JpaRepository<EventSchedule, Long> {
}
