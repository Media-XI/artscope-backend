package com.example.codebase.job;

import com.example.codebase.discord.DiscordMsgService;
import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionResponse;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionData;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionResponse;
import com.example.codebase.domain.exhibition.crawling.service.DetailEventCrawlingService;
import com.example.codebase.domain.exhibition.crawling.service.ExhibitionCrawlingService;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class JobService {

    private final MemberRepository memberRepository;

    private final ExhibitionCrawlingService exhibitionCrawlingService;

    private final DetailEventCrawlingService detailEventCrawlingService;

    private final DiscordMsgService discordMsgService;

    @Autowired
    public JobService(MemberRepository memberRepository,
                      ExhibitionCrawlingService exhibitionCrawlingService, DetailEventCrawlingService detailEventCrawlingService, DiscordMsgService discordMsgService) {
        this.memberRepository = memberRepository;
        this.exhibitionCrawlingService = exhibitionCrawlingService;
        this.detailEventCrawlingService = detailEventCrawlingService;
        this.discordMsgService = discordMsgService;
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

    @PostConstruct
    @Scheduled(cron = "0 3 * * * WED")
    public void getExhibitionListScheduler() {
        log.info("[getExhibitionListScheduler JoB] 전시회 리스트 크롤링 시작");
        try {
            List<XmlExhibitionResponse> xmlResponses = exhibitionCrawlingService.loadXmlDatas().get();
            Member admin = getAdmin();

            for (XmlExhibitionResponse xmlResponse : xmlResponses) {
                List<XmlExhibitionData> exhibitions = xmlResponse.getXmlExhibitions();

                for (XmlExhibitionData xmlExhibitionData : exhibitions) {
                    CompletableFuture<XmlDetailExhibitionResponse> xmlDetailExhibitionResponse = detailEventCrawlingService.loadAndParseXmlData(xmlExhibitionData);
                    detailEventCrawlingService.saveDetailExhibition(xmlDetailExhibitionResponse, admin);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("InterruptedException: {}", e.getMessage());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            log.error("ExecutionException: {}", cause.getMessage());
        }
    }

    private Member getAdmin() {
        return memberRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("관리자 계정이 없습니다."));
    }
}

