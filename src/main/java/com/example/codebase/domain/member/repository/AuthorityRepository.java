package com.example.codebase.domain.member.repository;

import com.example.codebase.domain.member.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
