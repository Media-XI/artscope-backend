package com.example.codebase.domain.Event.crawling.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.codebase.domain.Event.crawling.XmlResponseEntity;
import com.example.codebase.domain.Event.crawling.dto.eventResponse.XmlEventResponse;
import com.example.codebase.s3.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EventCrawlingService {
    private final RestTemplate restTemplate;

    @Value("${service.key}")
    private String serviceKey;


    private S3Service s3Service;

    @Autowired
    public EventCrawlingService(
            RestTemplate restTemplate, S3Service s3Service) {
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
    }

    public List<XmlEventResponse> loadXmlDatas() {
        try {
            List<XmlEventResponse> xmlResponseList = new ArrayList<>();
            int totalPage = 1;

            for (int currentPage = 1; currentPage <= totalPage; currentPage++) {
                XmlEventResponse xmlResponse = loadXmlDataForCurrentPage(currentPage);
                xmlResponseList.add(xmlResponse);

                if (currentPage == 1) {
                    int totalCount = xmlResponse.getMsgBody().getTotalCount();
                    totalPage = (int) Math.ceil((double) totalCount / 10);
                }
            }
            return xmlResponseList;
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private XmlEventResponse loadXmlDataForCurrentPage(int currentPage) throws IOException {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String savedFileName = String.format("event-backup/%s/전시공연정보_%s_%d.xml", currentDate, currentDate, currentPage);
        try {
            ResponseEntity<byte[]> object = s3Service.getObject(savedFileName);

            String body = new String(Objects.requireNonNull(object.getBody()));
            return XmlEventResponse.parse(body);
        } catch (AmazonS3Exception e) {
            log.info(e.getMessage());
            log.info("S3에 파일이 없습니다. 이벤트 목록 조회 API를 호출합니다.");

            Pair<XmlEventResponse, String> xmlExhibitionApiResponse = getXmlExhibitionApiResponse(currentPage, currentDate);
            XmlEventResponse xmlResponse = xmlExhibitionApiResponse.getFirst();

            byte[] file = xmlExhibitionApiResponse.getSecond().getBytes();
            s3Service.saveUploadFile(savedFileName, file);

            return xmlResponse;
        }
    }

    private Pair<XmlEventResponse, String> getXmlExhibitionApiResponse(int currentPage, String currentDate) {
        String url = String.format("http://www.culture.go.kr/openapi/rest/publicperformancedisplays/period?RequestTime=20100810:23003422&serviceKey=%s&cPage=%d&rows=100&from=%s&sortStdr=1", serviceKey, currentPage, currentDate);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        XmlResponseEntity xmlResponseEntity = new XmlResponseEntity(response.getBody(), response.getStatusCode());

        xmlResponseEntity.statusCodeCheck();

        XmlEventResponse xmlResponse = XmlEventResponse.parse(response.getBody());
        xmlResponse.statusCheck();
        return Pair.of(xmlResponse, Objects.requireNonNull(response.getBody()));
    }

}
