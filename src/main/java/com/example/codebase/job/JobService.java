package com.example.codebase.job;

import com.example.codebase.domain.event.crawling.dto.eventDetailResponse.XmlEventDetailResponse;
import com.example.codebase.domain.event.crawling.dto.eventResponse.XmlEventData;
import com.example.codebase.domain.event.crawling.dto.eventResponse.XmlEventResponse;
import com.example.codebase.domain.event.crawling.service.EventDetailCrawlingService;
import com.example.codebase.domain.event.crawling.service.EventCrawlingService;
import com.example.codebase.domain.event.entity.Event;
import com.example.codebase.domain.event.repository.EventRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JobService {

    private final MemberRepository memberRepository;

    private final EventCrawlingService eventCrawlingService;

    private final EventDetailCrawlingService eventDetailCrawlingService;

    private final EventRepository eventRepository;

    @Autowired
    public JobService(MemberRepository memberRepository,
                      EventCrawlingService eventCrawlingService, EventDetailCrawlingService eventDetailCrawlingService,
                      EventRepository eventRepository) {
        this.memberRepository = memberRepository;
        this.eventCrawlingService = eventCrawlingService;
        this.eventDetailCrawlingService = eventDetailCrawlingService;
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

    private Member getAdmin() {
        return memberRepository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("관리자 계정이 없습니다."));
    }

    @Scheduled(cron = "0 3 * * * WED")
    public void getEventListScheduler() throws IOException {
        LocalDate now = LocalDate.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        getEventList(currentDate);
    }

    @Transactional
    public void getEventList(String date) throws IOException {
        log.info("[getEventListScheduler JoB] 이벤트 리스트 크롤링 시작");
        LocalDateTime startTime = LocalDateTime.now();
        List<XmlEventResponse> xmlResponses = eventCrawlingService.loadXmlDatas();
        Member admin = getAdmin();

        List<Event> eventEntities = new ArrayList<>();
        for (XmlEventResponse xmlResponse : xmlResponses) {
            List<XmlEventData> events = xmlResponse.getXmlEvents();

            for (XmlEventData xmlEventData : events) {
                XmlEventDetailResponse xmlEventDetailResponse = eventDetailCrawlingService.loadAndParseXmlData(xmlEventData);
                Optional<Event> event = eventDetailCrawlingService.createEvent(xmlEventDetailResponse, admin);
                event.ifPresent(eventEntities::add);
            }
        }
        eventRepository.saveAll(eventEntities);

        LocalDateTime endTime = LocalDateTime.now();
        log.info("[getEventListScheduler JoB] 크롤링 소요시간: {} 초", endTime.getSecond() - startTime.getSecond());
        log.info("[getEventListScheduler JoB] 이벤트 리스트 크롤링 종료");
    }
}
