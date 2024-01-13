package com.example.codebase.domain.location.dto;

import com.example.codebase.domain.location.entity.Location;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDTO {

    private Long id;

    private Double latitude;

    private Double longitude;

    private String address;

    private String name;

    private String englishName;

    private String phoneNumber;

    private String webSiteUrl;

    private String snsUrl;

    private String authorName;

    private String authorUserName;

    private String authorProfileImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static LocationResponseDTO from(Location location) {
        String authorName = null;
        String authorUserName = null;
        String authorProfileImage = null;
        LocalDateTime createdTime = null;
        LocalDateTime updatedTime = null;

        if (location.getMember() != null) {
            authorName = location.getMember().getName();
            authorUserName = location.getMember().getUsername();
            authorProfileImage = location.getMember().getPicture();
        }

        if( location.getCreatedTime() != null) {
            createdTime = location.getCreatedTime();
        }

        if( location.getUpdatedTime() != null) {
            updatedTime = location.getUpdatedTime();
        }


        return LocationResponseDTO.builder()
                .id(location.getId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .name(location.getName())
                .englishName(location.getEnglishName())
                .phoneNumber(location.getPhoneNumber())
                .webSiteUrl(location.getWebSiteUrl())
                .snsUrl(location.getSnsUrl())
                .authorName(authorName)
                .authorUserName(authorUserName)
                .authorProfileImage(authorProfileImage)
                .createdTime(createdTime)
                .updatedTime(updatedTime)
                .build();
    }
}
