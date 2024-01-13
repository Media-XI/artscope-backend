package com.example.codebase.domain.location.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationCreateDTO {

    @NotNull(message = "위도 정보는 필수입니다.")
    @DecimalMin(value = "-90", message = "위도는 최소 -90도부터 가능합니다.")
    @DecimalMax(value = "90", message = "위도는 최대 90도까지 가능합니다.")
    private Double latitude;

    @NotNull(message = "경도 정보는 필수입니다.")
    @DecimalMin(value = "-180", message = "경도는 최소 -180도부터 가능합니다.")
    @DecimalMax(value = "180", message = "경도는 최대 180도까지 가능합니다.")
    private Double longitude;

    @NotEmpty(message = "주소는 필수입니다.")
    private String address;

    @NotEmpty(message = "이름은 필수입니다.")
    private String name;

    private String englishName;

    @NotEmpty(message = "연락처는 필수입니다.")
    private String phoneNumber;

    @Size(max = 255, message = "웹사이트 주소길이는 최대 255자까지 가능합니다.")
    private String webSiteUrl;

    @Size(max = 255, message = "sns 주소길이는 최대 255자까지 가능합니다.")
    private String snsUrl;
}
