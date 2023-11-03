package com.example.codebase.domain.location.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationCreateDTO {

  @NotEmpty(message = "위도 정보는 필수입니다.")
  @DecimalMin(value = "-90", message = "위도는 최소 -90도부터 가능합니다.")
  @DecimalMax(value = "90", message = "위도는 최대 90도까지 가능합니다.")
  private Double latitude;

  @NotEmpty(message = "경도 정보는 필수입니다.")
  @DecimalMin(value = "-180", message = "경도는 최소 -180도부터 가능합니다.")
  @DecimalMax(value = "180", message = "경도는 최대 180도까지 가능합니다.")
  private Double longitude;

  @NotEmpty(message = "주소는 필수입니다.")
  private String address;

  @NotEmpty(message = "이름은 필수입니다.")
  private String name;

  private String englishName;

  @NotBlank(message = "링크는 필수입니다.")
  @Size(max = 255, message = "링크는 최대 255자까지 가능합니다.")
  private String link;

  @NotEmpty(message = "연락처는 필수입니다.")
  private String phoneNumber;

  @NotEmpty(message = "웹사이트 주소는 필수입니다.")
  private String webSiteUrl;

  @NotEmpty(message = "SNS 주소는 필수입니다.")
  private String snsUrl;
}
