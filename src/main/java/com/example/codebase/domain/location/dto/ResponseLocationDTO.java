package com.example.codebase.domain.location.dto;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.location.entity.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
    ResponseLocationDTO dto = new ResponseLocationDTO();
    dto.setId(eventSchedule.getLocation().getId());
    dto.setLatitude(eventSchedule.getLocation().getLatitude());
    dto.setLongitude(eventSchedule.getLocation().getLongitude());
    dto.setAddress(eventSchedule.getLocation().getAddress());
    dto.setName(eventSchedule.getLocation().getName());
    dto.setEnglishName(eventSchedule.getLocation().getEnglishName());
    dto.setLink(eventSchedule.getLocation().getLink());
    dto.setPhoneNumber(eventSchedule.getLocation().getPhoneNumber());
    dto.setWebSiteUrl(eventSchedule.getLocation().getWebSiteUrl());
    dto.setSnsUrl(eventSchedule.getLocation().getSnsUrl());
    return dto;
  }

  public static ResponseLocationDTO of(Location location) {
    ResponseLocationDTO dto = new ResponseLocationDTO();
    dto.setId(location.getId());
    dto.setLatitude(location.getLatitude());
    dto.setLongitude(location.getLongitude());
    dto.setAddress(location.getAddress());
    dto.setName(location.getName());
    dto.setEnglishName(location.getEnglishName());
    dto.setLink(location.getLink());
    dto.setPhoneNumber(location.getPhoneNumber());
    dto.setWebSiteUrl(location.getWebSiteUrl());
    dto.setSnsUrl(location.getSnsUrl());
    return dto;
  }
}
