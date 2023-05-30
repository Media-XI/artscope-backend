package com.example.codebase.domain.auth.service;

import com.example.codebase.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {

    private final RedisUtil redisUtil;

    @Autowired
    public AuthService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public void authenticateMail(String email, String code) {
        String cachedCode = redisUtil.getData(email)
                .orElseThrow(() -> new IllegalArgumentException("인증번호가 만료되었습니다."));

        if (cachedCode.equals(code)) {
            redisUtil.deleteData(email);

            // TODO : 이메일 인증되었음 회원가입 완료
            // TODO: ROLE_GUEST 삭제하고 ROLE_USER 추가
        } else {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }
    }
}
