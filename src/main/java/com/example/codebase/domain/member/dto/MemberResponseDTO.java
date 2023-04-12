package com.example.codebase.domain.member.dto;

import com.example.codebase.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemberResponseDTO {

    private String username;

    private String name;

    private String email;

    private Optional<String> picture;

//    private Optional<String> oauthProvider;

    // private Optional<String> oauthProviderId;

    // private boolean activated;

    private String artistStatus;

    private String snsUrl;

    private String websiteUrl;

    private String introduction;

    private String history;

//    private Set<String> authrities;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;
//
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//    private LocalDateTime updatedTime;

    public static MemberResponseDTO from(Member member) {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setUsername(member.getUsername());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setPicture(Optional.ofNullable(member.getPicture()));
        //dto.setOauthProvider(Optional.ofNullable(String.valueOf(member.getOauthProvider())));
        // dto.setOauthProviderId(Optional.ofNullable(member.getOauthProviderId()));
        // dto.setActivated(member.isActivated());
        /* dto.setAuthrities(
                member.getAuthorities().stream()
                        .map(authority -> authority.getAuthority().getAuthorityName()
                        )
                        .collect(Collectors.toSet())
        ); */
        dto.setCreatedTime(member.getCreatedTime());
//        dto.setUpdatedTime(member.getUpdatedTime());
        dto.setArtistStatus(member.getArtistStatus().toString());
        dto.setSnsUrl(member.getSnsUrl());
        dto.setWebsiteUrl(member.getWebsiteUrl());
        dto.setIntroduction(member.getIntroduction());
        dto.setHistory(member.getHistory());

        return dto;
    }

}
