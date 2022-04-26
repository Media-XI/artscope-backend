package com.example.codebase.domain.testresult.repository;

import com.example.codebase.domain.testresult.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<TestResult, Long> {
}
