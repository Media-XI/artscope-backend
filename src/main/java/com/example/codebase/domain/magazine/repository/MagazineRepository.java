package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {
}
