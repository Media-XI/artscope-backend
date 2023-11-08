package com.example.codebase.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDTO {
    private String accessToken;

    private Long expiresIn;

    private String refreshToken;

    private Long refreshExpiresIn;

    private String token_type = "Bearer";
}
