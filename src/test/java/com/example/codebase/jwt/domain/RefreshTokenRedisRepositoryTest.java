package com.example.codebase.jwt.domain;

import com.example.codebase.domain.auth.entity.RefreshToken;
import com.example.codebase.domain.auth.repository.RefreshTokenRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.cloud.config.enabled=false")
class RefreshTokenRedisRepositoryTest {

    @Autowired
    RefreshTokenRedisRepository repo;

    @DisplayName("token redis 저장 success")
    @Test
    void tokenSave() {
        //given
        RefreshToken token = RefreshToken.builder()
                .userId("test")
                .refreshToken("test_token")
                .expiredTime(60)    //테스트용 1분
                .build();
        //when
        RefreshToken refreshToken = repo.save(token);
        //then
        RefreshToken findToken = repo.findById(token.getUserId()).get();
        assertEquals(refreshToken.getRefreshToken(), findToken.getRefreshToken());
    }

}