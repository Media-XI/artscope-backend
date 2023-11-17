package com.example.codebase.domain.location.entity;

import com.example.codebase.domain.location.dto.LocationCreateDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "location")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @Column(name = "location_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // TODO: NULLABLE
    @Column(name = "english_name", length = 255)
    private String englishName;

    @Column(name = "phone_number", nullable = false, length = 255)
    private String phoneNumber;

    @Column(name = "web_site_url", length = 255)
    private String webSiteUrl;

    @Column(name = "sns_url", length = 255)
    private String snsUrl;

    public static Location from(LocationCreateDTO dto) {
        return Location.builder()
            .latitude(dto.getLatitude())
            .longitude(dto.getLongitude())
            .address(dto.getAddress())
            .name(dto.getName())
            .englishName(dto.getEnglishName())
            .phoneNumber(dto.getPhoneNumber())
            .webSiteUrl(dto.getWebSiteUrl())
            .snsUrl(dto.getSnsUrl())
            .build();
    }
}
