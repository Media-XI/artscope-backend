package com.example.codebase.domain.magazine.repository;

import com.example.codebase.domain.magazine.entity.Magazine;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.team.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    @Query("SELECT m FROM Magazine m WHERE m.id = :id AND m.isDeleted = false")
    Optional<Magazine> findById(Long id);

    @Query("SELECT m FROM Magazine m WHERE m.member = :member AND m.team is null")
    Page<Magazine> findByMember(Member member, PageRequest pageRequest);

    @Query("SELECT m FROM Magazine m LEFT JOIN Follow f ON (f.follower= :member) WHERE f.followingMember = m.member")
    Page<Magazine> findByMemberToFollowing(Member member, PageRequest pageRequest);

    Page<Magazine> findByTeam(Team team, PageRequest pageRequest);
}