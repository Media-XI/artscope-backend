package com.example.codebase.domain.auth;

import com.example.codebase.domain.member.entity.oauth2.oAuthProvider;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final oAuthProvider registrationId;
    private final String oAuthProviderId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey, String oAuthProviderId, String name,
                           String email, String picture, oAuthProvider registrationId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.oAuthProviderId = oAuthProviderId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.registrationId = registrationId;
    }

    // OAuth2User에서 반환하는 정보는 Map임
    public static OAuthAttributes of(String registrationId,  // OAuth 제공자
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
            .oAuthProviderId(attributes.get("sub").toString())
            .name((String) attributes.get("name"))
            .email((String) attributes.get("email"))
            .picture((String) attributes.get("picture"))
            .attributes(attributes)
            .registrationId(oAuthProvider.google)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response"); // Json 필드 값 가져온다.

        return OAuthAttributes.builder()
            .oAuthProviderId(attributes.get("sub").toString())
            .name((String) response.get("name"))
            .email((String) response.get("email"))
            .picture((String) response.get("profile_image"))
            .attributes(response)
            .nameAttributeKey(userNameAttributeName)
            .registrationId(oAuthProvider.valueOf("naver"))
            .build();
    }

}
