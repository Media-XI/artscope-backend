package com.example.codebase.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDTO {
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private int refreshExpiresIn;
    private String token_type = "Bearer";
}
