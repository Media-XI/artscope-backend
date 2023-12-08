package com.example.codebase.domain.exhibition.crawling.service;

import com.example.codebase.domain.exhibition.crawling.XmlResponseEntity;
import com.example.codebase.domain.exhibition.crawling.dto.exhibitionResponse.XmlExhibitionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ExhibitionCrawlingService {
    private final RestTemplate restTemplate;

    @Value("${service.key}")
    private String serviceKey;

    @Autowired
    public ExhibitionCrawlingService(
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<XmlExhibitionResponse> loadXmlDatas() {
        try {
            List<XmlExhibitionResponse> xmlResponseList = new ArrayList<>();
            int totalPage = 1;

            for (int currentPage = 1; currentPage <= totalPage; currentPage++) {
                XmlExhibitionResponse xmlResponse = loadXmlDataForCurrentPage(currentPage);
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

    private XmlExhibitionResponse loadXmlDataForCurrentPage(int currentPage) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String url = String.format("http://www.culture.go.kr/openapi/rest/publicperformancedisplays/period?RequestTime=20100810:23003422&serviceKey=%s&cPage=%d&row=10&from=%s&sortStdr=1", serviceKey, currentPage, currentDate);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        XmlResponseEntity xmlResponseEntity = new XmlResponseEntity(response.getBody(), response.getStatusCode());

        xmlResponseEntity.statusCodeCheck();

        XmlExhibitionResponse xmlResponse = XmlExhibitionResponse.parse(response.getBody());
        xmlResponse.statusCheck();
        return xmlResponse;
    }

}
