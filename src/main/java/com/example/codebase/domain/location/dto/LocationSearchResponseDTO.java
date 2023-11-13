package com.example.codebase.domain.location.dto;

import com.example.codebase.domain.location.entity.Location;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationSearchResponseDTO {

    private long locationId;

    private String name;

    private String address;

    private String englishName;

    public static LocationSearchResponseDTO from(Location location) {
        return LocationSearchResponseDTO.builder()
            .locationId(location.getId())
            .name(location.getName())
            .address(location.getAddress())
            .englishName(location.getEnglishName())
            .build();
    }
}
