package com.example.codebase.domain.location.dto;

import com.example.codebase.domain.location.entity.Location;
import lombok.*;

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

    public static LocationResponseDTO from(Location location) {
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
            .build();
    }
}
