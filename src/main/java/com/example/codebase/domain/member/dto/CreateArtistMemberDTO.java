package com.example.codebase.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArtistMemberDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    private String snsUrl;

    private String websiteUrl;

    private String introduction;

    private String history;
}
