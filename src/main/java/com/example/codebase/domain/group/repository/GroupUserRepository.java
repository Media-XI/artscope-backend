package com.example.codebase.domain.group.repository;

import com.example.codebase.domain.group.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
}
