package com.example.codebase.job;

import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class JobService {

    private final MemberRepository memberRepository;

    @Autowired
    public JobService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Scheduled(cron = "0 0/30 * * * *") // 매 30분마다 삭제
    public void deleteNoneActivatedMembers() {
        log.info("[DeleteNoneActivatedMembers JoB] 비활성 시간이 30분이 지난 회원 삭제!");
        List<Member> members = memberRepository.findMembersByNoneActrivatedAndCreatedTimeAfter(false,
            LocalDateTime.now().minusMinutes(30));

        log.info("[DeleteNoneActivatedMembers JoB] 총 {} 개의 회원 삭제", members.size());
        for (Member member : members) {
            log.info("[DeleteNoneActivatedMembers JoB] username: {} email: {} 가입시간: {} 활성여부: {} 삭제합니다.",
                member.getUsername(), member.getEmail(), member.getCreatedTime(), member.isActivated());
            memberRepository.delete(member);
        }
    }
}

