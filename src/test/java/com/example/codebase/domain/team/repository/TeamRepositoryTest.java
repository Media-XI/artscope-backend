package com.example.codebase.domain.team.repository;

import com.example.codebase.domain.team.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    private Team deletedTeam;
    @BeforeEach
    public void setup() {
        deletedTeam = Team.builder()
                .name("그룹1")
                .address("주소")
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .description("우리 그룹은 어쩌구")
                .backgroundImage("http://cdn.artscope.kr/image")
                .profileImage("http://cdn.artscope.kr/image")
                .build();
        teamRepository.save(deletedTeam);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("삭제된 팀은 조회가 안된다.")
    @Test
    public void test01() throws Exception {
        // given
        teamRepository.delete(deletedTeam);

        // when
        Optional<Team> optionalTeam = teamRepository.findById(deletedTeam.getId());

        // then
        assertTrue(optionalTeam.isEmpty());
    }

}