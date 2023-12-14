package com.example.codebase.job;

import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionResponse;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionData;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionResponse;
import com.example.codebase.domain.exhibition.crawling.service.DetailEventCrawlingService;
import com.example.codebase.domain.exhibition.crawling.service.ExhibitionCrawlingService;
import com.example.codebase.domain.exhibition.entity.Event;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.EventRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.exhibition.service.EventService;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JobService {

    private final MemberRepository memberRepository;

    private final ExhibitionCrawlingService exhibitionCrawlingService;

    private final DetailEventCrawlingService detailEventCrawlingService;

    private final ExhibitionRepository exhibitionRepository;

    private final EventRepository eventRepository;

    @Autowired
    public JobService(MemberRepository memberRepository,
                      ExhibitionCrawlingService exhibitionCrawlingService, DetailEventCrawlingService detailEventCrawlingService, ExhibitionRepository exhibitionRepository,
                       EventRepository eventRepository) {
        this.memberRepository = memberRepository;
        this.exhibitionCrawlingService = exhibitionCrawlingService;
        this.detailEventCrawlingService = detailEventCrawlingService;
        this.exhibitionRepository = exhibitionRepository;
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 0/30 * * * *") // 매 30분마다 삭제
    @Transactional
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

    @Transactional
    public void getExhibitionListScheduler() throws IOException {
        log.info("[getExhibitionListScheduler JoB] 전시회 리스트 크롤링 시작");
        LocalDateTime startTime = LocalDateTime.now();
        List<XmlExhibitionResponse> xmlResponses = exhibitionCrawlingService.loadXmlDatas();
        Member admin = getAdmin();

        for (XmlExhibitionResponse xmlResponse : xmlResponses) {
            List<XmlExhibitionData> exhibitions = xmlResponse.getXmlExhibitions();
            List<Exhibition> exhibitionEntities = new ArrayList<>();

            for (XmlExhibitionData xmlExhibitionData : exhibitions) {
                XmlDetailExhibitionResponse xmlDetailExhibitionResponse = detailEventCrawlingService.loadAndParseXmlData(xmlExhibitionData);
                Exhibition exhibition = detailEventCrawlingService.createExhibition(xmlDetailExhibitionResponse, admin);
                exhibitionEntities.add(exhibition);
            }
            exhibitionRepository.saveAll(exhibitionEntities);
        }

        log.info("[getExhibitionListScheduler JoB] 전시회 리스트 크롤링 종료");
        LocalDateTime endTime = LocalDateTime.now();
        log.info("[getExhibitionListScheduler JoB] 크롤링 소요시간: {} 초", endTime.getSecond() - startTime.getSecond());
    }

    private Member getAdmin() {
        return memberRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("관리자 계정이 없습니다."));
    }

    @Scheduled(cron = "0 3 * * * WED")
    @Transactional
    public void getEventListScheduler() {
        log.info("[getEventListScheduler JoB] 이벤트 리스트 크롤링 시작");
        LocalDateTime startTime = LocalDateTime.now();
        List<XmlExhibitionResponse> xmlResponses = exhibitionCrawlingService.loadXmlDatas();
        Member admin = getAdmin();

        for (XmlExhibitionResponse xmlResponse : xmlResponses) {
            List<XmlExhibitionData> events = xmlResponse.getXmlExhibitions();
            List<Event> eventEntities = new ArrayList<>();

            for (XmlExhibitionData xmlExhibitionData : events) {
                XmlDetailExhibitionResponse xmlDetailEventResponse = null;
                try {
                    xmlDetailEventResponse = detailEventCrawlingService.loadAndParseXmlData(xmlExhibitionData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Event event = detailEventCrawlingService.createEvent(xmlDetailEventResponse, admin);
                eventEntities.add(event);
            }
            eventRepository.saveAll(eventEntities);
        }

        log.info("[getEventListScheduler JoB] 전시회 리스트 크롤링 종료");
        LocalDateTime endTime = LocalDateTime.now();
        log.info("[getEventListScheduler JoB] 크롤링 소요시간: {} 초", endTime.getSecond() - startTime.getSecond());
    }
}
