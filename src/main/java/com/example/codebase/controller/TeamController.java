package com.example.codebase.controller;

import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @PostMapping
    public ResponseEntity createTeam() {
        Team saved = teamRepository.save(Team.builder()
                .name("그룹1")
                .address("주소")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .description("우리 그룹은 어쩌구")
                .backgroundImage("http://cdn.artscope.kr/image")
                .profileImage("http://cdn.artscope.kr/image")
                .build());
        return new ResponseEntity(saved, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllTeam() {
        return new ResponseEntity(teamRepository.findAll(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteTeam(@RequestParam Long id) {
        teamRepository.deleteById(id);
        return new ResponseEntity("삭제됨", HttpStatus.OK);
    }
}
