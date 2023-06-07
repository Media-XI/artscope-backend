package com.example.codebase.domain.member.repository;


import com.example.codebase.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    /*
    * Deprecated!
    * Authorities를 Fetch.Lazy 에서 Eager로 변경하여 지연 로딩 없이 가져오므로
    * 해당 메소드는 사용하지 않아도 됨.
     */
    Optional<Member> findOneWithAuthoritiesByUsername(String username);
    Optional<Member> findByUsername(String username);
    Optional<Member> findByOauthProviderId(String oauthProviderId);
    Optional<Member> findByOauthProviderIdAndEmail(String oauthProviderId, String email);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    Optional<Member> findByEmailAndActivated(String email, boolean activated);

    @Query("SELECT m " +
            "FROM Member m " +
            "WHERE m.activated = ?1 and m.createdTime <= ?2")
    List<Member> findMembersByNoneActrivatedAndCreatedTimeAfter(boolean activated, LocalDateTime afterTime);
}
