package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
