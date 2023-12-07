package com.example.codebase.domain.exhibition.crawling.service;


import com.example.codebase.domain.exhibition.crawling.XmlResponseEntity;
import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionResponse;
import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionData;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionData;
import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.exhibition.repository.EventScheduleRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionMediaRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DetailEventCrawlingService {
    private RestTemplate restTemplate;
    private ExhibitionMediaRepository exhibitionMediaRepository;

    private LocationRepository locationRepository;

    private ExhibitionRepository exhibitionRepository;

    private EventScheduleRepository eventScheduleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${service.key}")
    private String serviceKey;

    @Autowired
    public DetailEventCrawlingService(RestTemplate restTemplate,
                                      LocationRepository locationRepository,
                                      ExhibitionRepository exhibitionRepository,
                                      EventScheduleRepository eventScheduleRepository) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.exhibitionRepository = exhibitionRepository;
        this.eventScheduleRepository = eventScheduleRepository;
    }


    @Async
    public CompletableFuture<XmlDetailExhibitionResponse> loadAndParseXmlData(XmlExhibitionData xmlExhibitionData) {
        return CompletableFuture.supplyAsync(() -> {
            XmlResponseEntity xmlResponseEntity = loadXmlDatas(xmlExhibitionData);
            return parseXmlData(xmlResponseEntity);
        });
    }

    private XmlResponseEntity loadXmlDatas(XmlExhibitionData xmlExhibitionData) {
        String url = String.format("http://www.culture.go.kr/openapi/rest/publicperformancedisplays/d/?serviceKey=%s&seq=%d", serviceKey, xmlExhibitionData.getSeq());

        ResponseEntity<String> response =restTemplate.getForEntity(url, String.class);
        XmlResponseEntity xmlResponseEntity = new XmlResponseEntity(response.getBody(), response.getStatusCode());

        xmlResponseEntity.statusCodeCheck();
        return xmlResponseEntity;
    }

    private XmlDetailExhibitionResponse parseXmlData(XmlResponseEntity xmlResponseEntity) {
        XmlDetailExhibitionResponse xmlDetailExhibitionResponse = XmlDetailExhibitionResponse.parse(xmlResponseEntity.getBody());
        responseStatusCheck(xmlDetailExhibitionResponse);
        return xmlDetailExhibitionResponse;
    }

    @Transactional
    public void saveDetailExhibition(CompletableFuture<XmlDetailExhibitionResponse> response, Member admin) {
        entityManager.createNativeQuery("SELECT 1;").getResultList();

        response.thenApply(XmlDetailExhibitionResponse::getDetailExhibitionData)
                .thenAccept(detailExhibitionData -> {
                    EventType eventType = checkEventType(detailExhibitionData);
                    Location location = findOrCreateLocation(detailExhibitionData);

                    Exhibition exhibition = findOrCreateExhibition(detailExhibitionData, admin);
                    ExhibitionMedia exhibitionMedia = ExhibitionMedia.from(detailExhibitionData, exhibition);

                    List<EventSchedule> eventSchedules = makeEventSchedule(detailExhibitionData, exhibition, location);

                    exhibition.setEventSchedules(eventSchedules);
                    exhibition.setType(eventType);
                    exhibition.addExhibitionMedia(exhibitionMedia);

                    exhibitionRepository.save(exhibition);

                })
                .exceptionally(ex -> {
                    log.info("An error occurred: " + ex.getMessage());
                    return null;
                });
    }

    private Exhibition findOrCreateExhibition(XmlDetailExhibitionData perforInfo, Member member) {
        return exhibitionRepository.findBySeq(perforInfo.getSeq())
                .map(existingExhibition -> {
            if (hasChanged(existingExhibition, perforInfo)) {
                deleteRelatedData(existingExhibition);
                return updateExhibition(existingExhibition, perforInfo, member);
            }
            return existingExhibition;
        }).orElseGet(() -> Exhibition.of(perforInfo, member));
    }

    private boolean hasChanged(Exhibition exhibition, XmlDetailExhibitionData perforInfo){
        return exhibition.hasChanged(perforInfo);
    }

    private Exhibition updateExhibition(Exhibition existingExhibition, XmlDetailExhibitionData perforInfo, Member member) {
        return existingExhibition.update(perforInfo, member);
    }

    private void deleteRelatedData(Exhibition exhibition) {
        for(EventSchedule eventSchedule : exhibition.getEventSchedules()) {
            eventSchedule.delete();
            eventScheduleRepository.delete(eventSchedule);
        }
        exhibitionMediaRepository.deleteAll(exhibition.getExhibitionMedias());
    }
    private void responseStatusCheck(XmlDetailExhibitionResponse response) {
        if (response.getComMsgHeader().getReturnCode().equals("00")) {
            return;
        }
        if (response.getComMsgHeader().getReturnCode().equals("30")) {
            throw new RuntimeException("서비스 키 관련 문제");
        }
        if (response.getComMsgHeader().getReturnCode().equals("22")) {
            throw new RuntimeException("API 호출 횟수 초과");
        }
        throw new RuntimeException("상세 이벤트 정보가 조회 되지 않음");
    }

    private EventType checkEventType(XmlDetailExhibitionData perforInfo) {
        return switch (perforInfo.getRealmName()) {
            case "전시", "미술" -> EventType.EXHIBITION;
            case "강연", "강의" -> EventType.LECTURE;
            case "영화", "연극", "뮤지컬", "음악", "국악", "무용" -> EventType.CONCERT;
            default -> EventType.STANDARD;
        };
    }

    private Location findOrCreateLocation(XmlDetailExhibitionData perforInfo) {
        return locationRepository.findByGpsXAndGpsY(perforInfo.getGpsX(), perforInfo.getGpsY())
                .orElseGet(() -> locationRepository.findByName(perforInfo.getRealmName())
                        .orElseGet(() -> {
                            Location newLocation = Location.from(perforInfo);
                            locationRepository.save(newLocation);
                            return newLocation;
                        }));
    }

    private List<EventSchedule> makeEventSchedule(XmlDetailExhibitionData perforInfo, Exhibition exhibition, Location location) {
        LocalDate startDate = LocalDate.parse(perforInfo.getStartDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate endDate = LocalDate.parse(perforInfo.getEndDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));

        return Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                .map(date -> EventSchedule.of(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), location, exhibition))
                .collect(Collectors.toList());
    }

}