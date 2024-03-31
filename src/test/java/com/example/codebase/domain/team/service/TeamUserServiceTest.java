package com.example.codebase.domain.team.service;

import com.example.codebase.domain.team.entity.Team;
import com.example.codebase.domain.team.entity.TeamUser;
import com.example.codebase.domain.team.repository.TeamRepository;
import com.example.codebase.domain.team.repository.TeamUserRepository;
import com.example.codebase.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TeamUserServiceTest {

    @InjectMocks
    private TeamUserService teamUserService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamUserRepository teamUserRepository;

    @Nested
    @DisplayName("findByTeamIdAndUsername 메소드 검증")
    class findByTeamIdAndUsername{

        @Test
        @DisplayName("팀이 존재하지 않을 때 NotFoundException 발생")
        void 팀이_존재하지_않을때() {
            //given
            Long testTeamId = 1L;
            String testUsername = "testUsername";

            given(teamRepository.existsById(testTeamId)).willReturn(false); // 해당 요청시 false 반환

            // when
            assertThatThrownBy(() -> teamUserService.findByTeamIdAndUsername(testTeamId, testUsername))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 팀이 존재하지 않습니다.");

        }

        @Test
        @DisplayName("팀에 속해있지 않을 때 NotFoundException 발생")
        void 팀에_속해있지_않을때() {
            //given
            Long testTeamId = 1L;
            String testUsername = "testUsername";

            given(teamRepository.existsById(testTeamId)).willReturn(true);
            given(teamUserRepository.findByTeamIdAndUsername(testTeamId, testUsername)).willReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> teamUserService.findByTeamIdAndUsername(testTeamId, testUsername))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 팀에 속해있지 않습니다.");
        }

        @Test
        @DisplayName("팀에 속해있을 때")
        void 팀에_속해있을때() {
            //given
            Long testTeamId = 1L;
            String testUsername = "testUsername";
            Team team = Team.builder().id(testTeamId).build();
            TeamUser teamUser = TeamUser.builder().team(team).build();

            given(teamRepository.existsById(testTeamId)).willReturn(true);
            given(teamUserRepository.findByTeamIdAndUsername(testTeamId, testUsername)).willReturn(Optional.ofNullable(teamUser));

            // when
            teamUserService.findByTeamIdAndUsername( testTeamId, testUsername);

            // then
            verify(teamRepository).existsById(teamUser.getTeam().getId()); // 해당 요청이 호출되었는지 확인
            verify(teamUserRepository).findByTeamIdAndUsername(testTeamId, testUsername);
        }
    }
}
