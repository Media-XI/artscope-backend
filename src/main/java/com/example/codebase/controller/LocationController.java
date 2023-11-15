package com.example.codebase.controller;

import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.dto.LocationResponseDTO;
import com.example.codebase.domain.location.dto.LocationsSearchResponseDTO;
import com.example.codebase.domain.location.service.LocationService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.PositiveOrZero;

@ApiOperation(value = "위치", notes = "위치 관련 API")
@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @ApiOperation(value = "특정 장소 추가", notes = "[ADMIN, CURATOR, ARTIST] 특정 장소에 대한 위치 정보를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST','ROLE_CURATOR')")
    @PostMapping
    public ResponseEntity createLocation(@RequestBody LocationCreateDTO dto) {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        LocationResponseDTO location = locationService.createLocation(dto, username);
        return new ResponseEntity(location, HttpStatus.CREATED);
    }

    @ApiOperation(value = "특정 장소 조회", notes = "특정 장소에 대한 위치 정보를 조회합니다.")
    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponseDTO> getLocation(
        @PathVariable("locationId") Long locationId) {
        LocationResponseDTO location = locationService.getLocation(locationId);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 장소 정보 삭제", notes = "[ADMIN] 특정 장소에 대한 위치 정보를 삭제합니다.")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/{locationId}")
    public ResponseEntity deleteLocation(@PathVariable("locationId") Long locationId) {
        String username =
            SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        locationService.deleteLocation(locationId, username);

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "특정 장소 검색", notes = "[모든 사용자] 특정 장소에 대한 정보를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity findLocationByKeyword(
        @RequestParam String keyword,
        @PositiveOrZero @RequestParam(defaultValue = "0") int page,
        @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        LocationsSearchResponseDTO searchResponseDTO =
            locationService.findLocationByKeyword(keyword, page, size);

        return new ResponseEntity(searchResponseDTO, HttpStatus.OK);
    }
}
