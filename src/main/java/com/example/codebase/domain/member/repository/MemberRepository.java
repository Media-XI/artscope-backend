package com.example.codebase.domain.member.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.RoleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByUsername(String username);

    @Query(" SELECT m " +
        " FROM Member m " +
        " WHERE m.username = ?1 OR m.oauthProviderId = ?1")
    Optional<Member> findByUsernameOrOauthProviderId(String username);

    Optional<Member> findByOauthProviderId(String oauthProviderId);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Optional<Member> findByEmailAndActivated(String email, boolean activated);

    @Query("SELECT m " + "FROM Member m " + "WHERE m.activated = ?1 and m.createdTime <= ?2")
    List<Member> findMembersByNoneActrivatedAndCreatedTimeAfter(
        boolean activated, LocalDateTime afterTime);

    @Query("SELECT m FROM Member m WHERE m.email = ?1")
    Member findByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.email LIKE  ?1%")
    Page<Member> searchByEmail(String email, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.username LIKE ?1%")
    Page<Member> searchByUsername(String username, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.name LIKE ?1%")
    Page<Member> searchByName(String username, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.roleStatus = :roleStatus")
    Page<Member> findAllByRoleStatus(RoleStatus roleStatus, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.roleStatus = ?1 OR m.roleStatus = ?2")
    Page<Member> findAllByRoleStatus(RoleStatus pending1, RoleStatus pending2, Pageable pageable);

}
