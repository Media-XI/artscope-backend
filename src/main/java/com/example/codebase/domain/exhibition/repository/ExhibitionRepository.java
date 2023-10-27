package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.Exhibition;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @Query("select e from Exhibition e where e.id = :id and e.enabled = true")
    Optional<Exhibition> findById(Long id);

    @Query("select e from Exhibition e where e.enabled = true")
    List<Exhibition> findAll();

    @Query("select e from Exhibition e where e.enabled = true " +
            "and (:startDate is null or e.startDate >= :startDate) " +
            "and (:endDate is null or e.endDate <= :endDate)")
    Page<Exhibition> findByStartAndEndDate(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);


}
