package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

}
