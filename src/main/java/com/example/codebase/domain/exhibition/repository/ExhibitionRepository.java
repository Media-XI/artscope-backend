package com.example.codebase.domain.exhibition.repository;

import com.example.codebase.domain.exhibition.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {}
