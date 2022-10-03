package com.example.codebase.jwt.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "refreshToken")
public class RefreshToken {
    @Id
    private String userId;
    private String refreshToken;

    @TimeToLive
    private long expiredTime;
}
