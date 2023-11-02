package com.example.codebase.domain.location.entity;

import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.location.dto.CreateLocationDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  @Column(name = "english_name", length = 255)
  private String englishName;

  @Column(name = "link", nullable = false, length = 255)
  private String link;

  @Column(name = "phone_number", nullable = false, length = 255)
  private String phoneNumber;

  @Column(name = "web_site_url", nullable = false, length = 255)
  private String webSiteUrl;

  @Column(name = "sns_url", nullable = false, length = 255)
  private String snsUrl;

  public static Location of (CreateLocationDTO dto) {
    return Location.builder()
        .latitude(dto.getLatitude())
        .longitude(dto.getLongitude())
        .address(dto.getAddress())
        .name(dto.getName())
        .englishName(dto.getEnglishName())
        .link(dto.getLink())
        .phoneNumber(dto.getPhoneNumber())
        .webSiteUrl(dto.getWebSiteUrl())
        .snsUrl(dto.getSnsUrl())
        .build();
  }

}
