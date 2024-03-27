package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.MagazineCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MagazineCategoryRepository extends JpaRepository<MagazineCategory, Long> {

    @Override
    @EntityGraph(attributePaths = {"children"})
    @Query("SELECT mc FROM MagazineCategory mc WHERE mc.parent IS NULL")
    List<MagazineCategory> findAll();

    @EntityGraph(attributePaths = {"children"})
    @Query("SELECT mc FROM MagazineCategory mc WHERE mc.slug = :slug")
    List<MagazineCategory> findBySlugWithChild(String slug);

    boolean existsByNameAndParent(String name, MagazineCategory parentCategory);

    @Query("SELECT COUNT(m) FROM Magazine m WHERE m.category = :category")
    Long countMagazineByCategory(MagazineCategory category);

    boolean existsBySlug(String slug);

    Optional<MagazineCategory> findBySlug(String slug);
}
