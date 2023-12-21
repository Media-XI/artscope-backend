package com.example.codebase.domain.Event.crawling.service;


import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.codebase.domain.Event.crawling.XmlResponseEntity;
import com.example.codebase.domain.Event.crawling.dto.eventDetailResponse.XmlEventDetailResponse;
import com.example.codebase.domain.Event.crawling.dto.eventDetailResponse.XmlEventDetailData;
import com.example.codebase.domain.Event.crawling.dto.eventResponse.XmlEventData;
import com.example.codebase.domain.Event.entity.*;
import com.example.codebase.domain.Event.repository.EventRepository;
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
public class EventDetailCrawlingService {
    private RestTemplate restTemplate;

    private S3Service s3Service;

    private LocationRepository locationRepository;

    private EventRepository eventRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${service.key}")
    private String serviceKey;

    @Autowired
    public EventDetailCrawlingService(RestTemplate restTemplate,
                                      LocationRepository locationRepository,
                                      EventRepository eventRepository, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
        this.s3Service = s3Service;
    }


    public XmlEventDetailResponse loadAndParseXmlData(XmlEventData xmlEventData) throws IOException {
        XmlResponseEntity xmlResponseEntity = loadXmlDatas(xmlEventData);
        return parseXmlData(xmlResponseEntity);
    }

    private XmlResponseEntity loadXmlDatas(XmlEventData xmlEventData) throws IOException {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String saveFileName = String.format("event-backup/event-detail-backup/%s/공연상세정보_%d.xml", currentDate, xmlEventData.getSeq());

        try {
            ResponseEntity<byte[]> object = s3Service.getObject(saveFileName);

            String body = new String(Objects.requireNonNull(object.getBody()));
            return new XmlResponseEntity(body, HttpStatus.OK);
        } catch (AmazonS3Exception e) {
            log.info(e.getMessage());
            log.info("S3에 파일이 없습니다. API를 호출합니다.");

            Pair<XmlResponseEntity, String> apiResponse = getXmlDetailEventApiResponse(xmlEventData);

            byte[] file = apiResponse.getSecond().getBytes();
            s3Service.saveUploadFile(saveFileName, file);
            return apiResponse.getFirst();
        }
    }

    private Pair<XmlResponseEntity, String> getXmlDetailEventApiResponse(XmlEventData xmlEventData) {
        String url = String.format("http://www.culture.go.kr/openapi/rest/publicperformancedisplays/d/?serviceKey=%s&seq=%d", serviceKey, xmlEventData.getSeq());

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        XmlResponseEntity xmlResponseEntity = new XmlResponseEntity(response.getBody(), response.getStatusCode());

        xmlResponseEntity.statusCodeCheck();
        return Pair.of(xmlResponseEntity, Objects.requireNonNull(response.getBody()));
    }

    private XmlEventDetailResponse parseXmlData(XmlResponseEntity xmlResponseEntity) {
        XmlEventDetailResponse xmlEventDetailResponse = XmlEventDetailResponse.parse(xmlResponseEntity.getBody());
        responseStatusCheck(xmlEventDetailResponse);
        return xmlEventDetailResponse;
    }


    public Event createEvent(XmlEventDetailResponse response, Member admin) {
        XmlEventDetailData detailEventData = response.getMsgBody().getDetailExhibitionData();

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

    private Event findOrCreateEvent(XmlEventDetailData eventData, Member admin) {
        return eventRepository
                .findBySeq(eventData.getSeq())
                .orElseGet(() -> Event.of(eventData, admin));
    }

    private void responseStatusCheck(XmlEventDetailResponse response) {
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

    private EventType checkEventType(XmlEventDetailData perforInfo) {
        return switch (perforInfo.getRealmName()) {
            case "전시", "미술" -> EventType.EXHIBITION;
            case "강연", "강의" -> EventType.LECTURE;
            case "영화", "연극", "뮤지컬", "음악", "국악", "무용" -> EventType.CONCERT;
            default -> EventType.STANDARD;
        };
    }

    private Location findOrCreateLocation(XmlEventDetailData perforInfo) {
        return locationRepository.findByGpsXAndGpsYOrAddress(perforInfo.getGpsX(), perforInfo.getGpsY(), perforInfo.getPlaceAddr())
                .orElseGet(() -> {
                    Location newLocation = Location.from(perforInfo);
                    locationRepository.save(newLocation);
                    return newLocation;
                });
    }

}