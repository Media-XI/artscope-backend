package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkMediaRepository extends JpaRepository<ArtworkMedia, Long> {
}
