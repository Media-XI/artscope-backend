package com.example.codebase.domain.exhibition.crawling.service;


import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.codebase.domain.exhibition.crawling.XmlResponseEntity;
import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionResponse;
import com.example.codebase.domain.exhibition.crawling.dto.detailExhbitionResponse.XmlDetailExhibitionData;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionData;
import com.example.codebase.domain.exhibition.entity.*;
import com.example.codebase.domain.exhibition.repository.EventRepository;
import com.example.codebase.domain.exhibition.repository.EventScheduleRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionMediaRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.s3.S3Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DetailEventCrawlingService {
    private RestTemplate restTemplate;
    private ExhibitionMediaRepository exhibitionMediaRepository;

    private S3Service s3Service;

    private LocationRepository locationRepository;

    private ExhibitionRepository exhibitionRepository;

    private EventScheduleRepository eventScheduleRepository;

    private EventRepository eventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${service.key}")
    private String serviceKey;

    @Autowired
    public DetailEventCrawlingService(RestTemplate restTemplate,
                                      LocationRepository locationRepository,
                                      ExhibitionRepository exhibitionRepository,
                                      EventScheduleRepository eventScheduleRepository,
                                      EventRepository eventRepository, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.exhibitionRepository = exhibitionRepository;
        this.eventScheduleRepository = eventScheduleRepository;
        this.eventRepository = eventRepository;
        this.s3Service = s3Service;
    }


    public XmlDetailExhibitionResponse loadAndParseXmlData(XmlExhibitionData xmlExhibitionData) throws IOException {
        XmlResponseEntity xmlResponseEntity = loadXmlDatas(xmlExhibitionData);
        return parseXmlData(xmlResponseEntity);
    }

    private XmlResponseEntity loadXmlDatas(XmlExhibitionData xmlExhibitionData) throws IOException {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String saveFileName = String.format("event-backup/event-detail-backup/%s/공연상세정보_%d.xml", currentDate, xmlExhibitionData.getSeq());

        try {
            ResponseEntity<byte[]> object = s3Service.getObject(saveFileName);

            String body = new String(Objects.requireNonNull(object.getBody()));
            return new XmlResponseEntity(body, HttpStatus.OK);
        } catch (AmazonS3Exception e) {
            log.info(e.getMessage());
            log.info("S3에 파일이 없습니다. API를 호출합니다.");

            Pair<XmlResponseEntity, String> apiResponse = getXmlDetailEventApiResponse(xmlExhibitionData);

            byte[] file = apiResponse.getSecond().getBytes();
            s3Service.saveUploadFile(saveFileName, file);
            return apiResponse.getFirst();
        }
    }

    private Pair<XmlResponseEntity, String> getXmlDetailEventApiResponse(XmlExhibitionData xmlExhibitionData) {
        String url = String.format("http://www.culture.go.kr/openapi/rest/publicperformancedisplays/d/?serviceKey=%s&seq=%d", serviceKey, xmlExhibitionData.getSeq());

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        XmlResponseEntity xmlResponseEntity = new XmlResponseEntity(response.getBody(), response.getStatusCode());

        xmlResponseEntity.statusCodeCheck();
        return Pair.of(xmlResponseEntity, Objects.requireNonNull(response.getBody()));
    }

    private XmlDetailExhibitionResponse parseXmlData(XmlResponseEntity xmlResponseEntity) {
        XmlDetailExhibitionResponse xmlDetailExhibitionResponse = XmlDetailExhibitionResponse.parse(xmlResponseEntity.getBody());
        responseStatusCheck(xmlDetailExhibitionResponse);
        return xmlDetailExhibitionResponse;
    }

    public Exhibition createExhibition(XmlDetailExhibitionResponse response, Member admin) {
        XmlDetailExhibitionData detailExhibitionData = response.getMsgBody().getDetailExhibitionData();
        EventType eventType = checkEventType(detailExhibitionData);

        Location location = findOrCreateLocation(detailExhibitionData);
        Exhibition exhibition = findOrCreateExhibition(detailExhibitionData, admin);

        if (exhibition.isPersist()) {
            if (hasChanged(exhibition, detailExhibitionData)) {
                deleteRelatedData(exhibition);
                updateExhibition(exhibition, detailExhibitionData, admin);
            }
            return exhibition;
        }

        ExhibitionMedia exhibitionMedia = ExhibitionMedia.from(detailExhibitionData, exhibition);
        List<EventSchedule> eventSchedules = makeEventSchedule(detailExhibitionData, exhibition, location);

        exhibition.setEventSchedules(eventSchedules);
        exhibition.setType(eventType);
        exhibition.addExhibitionMedia(exhibitionMedia);

        return exhibition;
    }

    public Event createEvent(XmlDetailExhibitionResponse response, Member admin) {
        XmlDetailExhibitionData detailEventData = response.getMsgBody().getDetailExhibitionData();

        EventType eventType = checkEventType(detailEventData);

        Location location = findOrCreateLocation(detailEventData);
        Event event = findOrCreateEvent(detailEventData, admin);

        if (event.isPersist()) {
            event.updateEventIfChanged(detailEventData, location);
            return event;
        }

        EventMedia eventMedia = EventMedia.from(detailEventData, event);

        event.setType(eventType);
        event.addEventMedia(eventMedia);
        event.setLocation(location);

        return event;
    }

    private Event findOrCreateEvent(XmlDetailExhibitionData eventData, Member admin) {
        return eventRepository
                .findBySeq(eventData.getSeq())
                .orElseGet(() -> Event.of(eventData, admin));
    }

    private Exhibition findOrCreateExhibition(XmlDetailExhibitionData perforInfo, Member member) {
        return exhibitionRepository
                .findBySeq(perforInfo.getSeq())
                .orElseGet(() -> Exhibition.of(perforInfo, member));
    }

    private boolean hasChanged(Exhibition exhibition, XmlDetailExhibitionData perforInfo) {
        return exhibition.hasChanged(perforInfo);
    }

    private void updateExhibition(Exhibition existingExhibition, XmlDetailExhibitionData perforInfo, Member member) {
        existingExhibition.update(perforInfo, member);
    }

    private void deleteRelatedData(Exhibition exhibition) {
        for (EventSchedule eventSchedule : exhibition.getEventSchedules()) {
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
                .orElseGet(() -> locationRepository.findByName(perforInfo.getPlaceAddr())
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