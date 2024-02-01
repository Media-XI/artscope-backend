package com.example.codebase.domain.follow.repository;

import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.follow.entity.FollowIds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowIds> {
}
