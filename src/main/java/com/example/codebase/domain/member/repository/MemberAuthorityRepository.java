package com.example.codebase.domain.member.repository;

import com.example.codebase.domain.member.entity.MemberAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthorityRepository extends JpaRepository<MemberAuthority, Long> {

}
