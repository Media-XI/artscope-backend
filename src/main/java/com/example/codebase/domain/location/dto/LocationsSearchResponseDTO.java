package com.example.codebase.domain.location.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LocationsSearchResponseDTO {
    List<LocationSearchResponseDTO> locations;

    PageInfo pageInfo;

    public static LocationsSearchResponseDTO of(
        List<LocationSearchResponseDTO> dtos, PageInfo pageInfo) {
        LocationsSearchResponseDTO locationsSearchResponseDTO = new LocationsSearchResponseDTO();
        locationsSearchResponseDTO.setLocations(dtos);
        locationsSearchResponseDTO.setPageInfo(pageInfo);
        return locationsSearchResponseDTO;
    }
}
