package com.example.codebase.domain.follow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.RegEx;

public class FollowRequest {

    public enum FollowEntityUrn {
        MEMBER("urn:member"),
        TEAM("urn:team");
        private final String resource;

        @Getter
        private String id;

        FollowEntityUrn(String resource) {
            this.resource = resource;
            this.id = null;
        }

        public static FollowEntityUrn from(String urn) {
            try {
                if (!urn.startsWith("urn:")) {
                    throw new IllegalArgumentException();
                }
                String resource = urn.split(":")[1];
                String id = urn.split(":")[2];

                FollowEntityUrn followEntityUrn = FollowEntityUrn.valueOf(resource.toUpperCase());
                followEntityUrn.id = id;
                return followEntityUrn;
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("유효하지 않은 URN 입니다.");
            }
        }

        public boolean isMember() {
            return this == MEMBER;
        }

        public boolean isTeam() {
            return this == TEAM;
        }
    }

    @Getter
    @Setter
    public static class Create {

        @NotBlank(message = "URN을 입력해주세요.")
        @Pattern(regexp = "urn:[a-z]:+", message = "올바른 URN 형식이 아닙니다.")
        private String urn;
    }
}
