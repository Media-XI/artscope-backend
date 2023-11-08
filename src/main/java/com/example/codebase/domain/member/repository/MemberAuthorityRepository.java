package com.example.codebase.domain.member.repository;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthorityRepository extends JpaRepository<MemberAuthority, Long> {

    Optional<MemberAuthority> findByMember(Member member);
}
