package com.example.codebase.domain.member.repository;

import com.example.codebase.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("비활성화 회원 삭제 테스트")
    @Test
    void deleteNoneActivatedMembers() {
        // given
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Member member = Member.builder()
                    .username("test" + i)
                    .email("test" + i + "@test.com")
                    .activated(false)
                    .createdTime(LocalDateTime.now().minusMinutes(30 + i))
                    .build();
            members.add(member);
        }
        for (int i = 0; i < 3; i++) {
            Member member = Member.builder()
                    .username("test2" + i)
                    .email("test2" + i + "@test.com")
                    .activated(false)
                    .createdTime(LocalDateTime.now().minusMinutes(i * 10))
                    .build();
            members.add(member);
        }
        memberRepository.saveAll(members);

        // when
        List<Member> noneActivatedMembers = memberRepository.findMembersByNoneActrivatedAndCreatedTimeAfter(false, LocalDateTime.now().minusMinutes(30));
        memberRepository.deleteAll(noneActivatedMembers);

        // then
        List<Member> after = memberRepository.findAll();
        assertEquals(3, after.size());
    }


}