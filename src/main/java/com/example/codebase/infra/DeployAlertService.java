package com.example.codebase.infra;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDateTime;

@Slf4j
public class DeployAlertService {

    @Value("${webhook.url}")
    private String webhookUrl;

    @Value("${build.info}")
    private String buildInfo;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public void deployAlert() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            healthCheck(restTemplate);
            webHook(restTemplate, makeMessage());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private void healthCheck(RestTemplate restTemplate) {
        String url = "http://localhost:8080/api/test/ping";
        String result = restTemplate.getForObject(url, String.class);

        assert result != null;
        if (!result.equals("pong!")) {
            throw new RuntimeException("서버가 정상적으로 배포되지 않았습니다.");
        }
    }

    private String makeMessage() {
        StringBuilder sb = new StringBuilder();
        String json = String.format("""
                {
                  "embeds": [
                    {
                      "author": {
                        "name": "백엔드 배포 알리미",
                        "icon_url": "https://i.imgur.com/R66g1Pe.jpg"
                      },
                      "title:": "배포 알림",
                      "description": "백엔드 서버가 배포 되었습니다.",
                      "color": "1127128",
                      "fields": [
                        {
                          "name": "서버 환경",
                          "value": "%s", 
                          "inline": true
                        },
                        {
                          "name": "서버 상태",
                          "value": "%s",
                          "inline": true
                        },
                        {
                            "name": "빌드 정보",
                            "value": "%s",
                            "inline": true
                        },
                        {
                            "name": "서버 시간",
                            "value": "%s"
                            },
                        {
                          "name": "저장소",
                          "value": "https://github.com/Media-XI/artscope-backend"
                        }
                      ],
                      "footer": {
                        "text": "Artscope Backend Server"
                      }
                    }
                  ]
                }
                """, activeProfile, "정상 배포", buildInfo, LocalDateTime.now());
        sb.append(json);
        return sb.toString();
    }

    private void webHook(RestTemplate restTemplate, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(message, headers);
        String string = restTemplate.postForObject(webhookUrl, entity, String.class);
        if (string != null) {
            throw new RuntimeException("웹훅 전송에 실패하였습니다.");
        }
    }
}
