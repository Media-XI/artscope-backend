package com.example.codebase.domain.location.dto;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.location.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseLocationDTO {

  private Long id;

  private Double latitude;

  private Double longitude;

  private String address;

  private String name;

  private String englishName;

  private String link;

  private String phoneNumber;

  private String webSiteUrl;

  private String snsUrl;

  public static ResponseLocationDTO from(EventSchedule eventSchedule) {
    return ResponseLocationDTO.builder()
        .id(eventSchedule.getLocation().getId())
        .latitude(eventSchedule.getLocation().getLatitude())
        .longitude(eventSchedule.getLocation().getLongitude())
        .address(eventSchedule.getLocation().getAddress())
        .name(eventSchedule.getLocation().getName())
        .englishName(eventSchedule.getLocation().getEnglishName())
        .link(eventSchedule.getLocation().getLink())
        .phoneNumber(eventSchedule.getLocation().getPhoneNumber())
        .webSiteUrl(eventSchedule.getLocation().getWebSiteUrl())
        .snsUrl(eventSchedule.getLocation().getSnsUrl())
        .build();
  }

  public static ResponseLocationDTO of(Location location) {
    return ResponseLocationDTO.builder()
        .id(location.getId())
        .latitude(location.getLatitude())
        .longitude(location.getLongitude())
        .address(location.getAddress())
        .name(location.getName())
        .englishName(location.getEnglishName())
        .link(location.getLink())
        .phoneNumber(location.getPhoneNumber())
        .webSiteUrl(location.getWebSiteUrl())
        .snsUrl(location.getSnsUrl())
        .build();
  }
}