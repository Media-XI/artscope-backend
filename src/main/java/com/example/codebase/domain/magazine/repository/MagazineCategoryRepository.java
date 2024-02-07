package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.MagazineCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineCategoryRepository extends JpaRepository<MagazineCategory, Long> {
}
