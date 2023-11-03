package com.example.codebase.controller;

import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.dto.LocationResponseDTO;
import com.example.codebase.domain.location.service.LocationService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ApiOperation(value = "위치", notes = "위치 관련 API")
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @ApiOperation(value = "특정 장소 추가", notes = "[ADMIN],[ARTIST] 특정 장소에 대한 위치 정보를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_ADMIN', 'ROLE_ARTIST')")
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
            @PathVariable("locationId") Long locationId,
            @PositiveOrZero @RequestParam int page,
            @PositiveOrZero @RequestParam int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        LocationResponseDTO location =
                locationService.getLocation(locationId, page, size, sortDirection);
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
}
