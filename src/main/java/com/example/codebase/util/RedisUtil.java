package com.example.codebase.util;

import com.example.codebase.domain.notification.dto.NotificationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Component
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisUtil(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<String> getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return Optional.ofNullable(valueOperations.get(key));
    }

    // 유효시간 동안 Data 저장
    public void setDataAndExpire(String key, String value, long durationMilliS) { // 유효시간은 밀리세컨드 단위
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(durationMilliS);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    // NotificationMessage
    public void saveNotificationMessage(String key, NotificationResponse.EventMessage eventMessage, long expireDuration) {
        try {
            String json = objectMapper.writeValueAsString(eventMessage);
            redisTemplate.opsForValue().set(key, json, Duration.ofMillis(expireDuration));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 중 에러 발생", e);
        }
    }

    public NotificationResponse.EventMessage getNotificationMessage(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            throw new RuntimeException("저장된 데이터가 없습니다");
        }
        try {
            return objectMapper.readValue(json, NotificationResponse.EventMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("JSON 읽는 중 에러 발생", e);
        }
    }

}
