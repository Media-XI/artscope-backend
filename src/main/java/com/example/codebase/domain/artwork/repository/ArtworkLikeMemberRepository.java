package com.example.codebase.domain.artwork.repository;

import com.example.codebase.domain.artwork.entity.ArtworkLikeMember;
import com.example.codebase.domain.artwork.entity.ArtworkLikeMemberId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ArtworkLikeMemberRepository extends JpaRepository<ArtworkLikeMember, ArtworkLikeMemberId> {

    @Query("select count(*) from ArtworkLikeMember alm where alm.artwork.id = ?1")
    Integer getLike(Long artworkId);

    Integer countByArtworkId(Long id);

    Page<ArtworkLikeMember> findAllByMemberId(UUID id, Pageable pageable);

    Page<ArtworkLikeMember> findAllByArtworkId(Long id, Pageable pageable);
}