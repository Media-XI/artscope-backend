package com.example.codebase.domain.agora.repository;

import com.example.codebase.domain.agora.entity.Agora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AgoraRepository extends JpaRepository<Agora, Long> {

}
