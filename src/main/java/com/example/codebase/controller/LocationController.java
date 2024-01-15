package com.example.codebase.controller;

import com.example.codebase.domain.location.dto.*;
import com.example.codebase.domain.location.service.LocationService;
import com.example.codebase.domain.search.dto.SearchResponseDTO;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.PositiveOrZero;

@Tag(name = "Location", description = "장소 API")
@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "장소 생성", description = "[인증 받은 유저] 장소를 생성합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity createLocation(@Valid @RequestBody LocationCreateDTO dto) {
        String username =
                SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));
        LocationResponseDTO location = locationService.createLocation(dto, username);
        return new ResponseEntity(location, HttpStatus.CREATED);
    }

    @Operation(summary = "장소 조회", description = "[모든 사용자] 특정 장소에 대한 정보를 조회합니다.")
    @GetMapping("/{locationId}")
    public ResponseEntity<LocationResponseDTO> getLocation(
            @PathVariable("locationId") Long locationId) {
        LocationResponseDTO location = locationService.getLocation(locationId);
        return new ResponseEntity<>(location, HttpStatus.OK);
    }

    @Operation(summary = "장소 삭제", description = "[ADMIN, 장소 최초 생성자] 특정 장소를 삭제합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{locationId}")
    public ResponseEntity deleteLocation(@PathVariable("locationId") Long locationId) {
        String username =
                SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        locationService.deleteLocation(locationId, username);

        return new ResponseEntity("성공적으로 삭제되었습니다", HttpStatus.OK);
    }

    @Operation(summary = "장소 검색", description = "[모든 사용자] 키워드, 유저 이름으로 장소를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity findLocationByKeyword(
            @RequestParam(required = false) String keyword,
            @PositiveOrZero @RequestParam(defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(defaultValue = "10") int size) {

        LocationsSearchResponseDTO searchResponseDTO = locationService.searchLocation(keyword, page, size);

        return new ResponseEntity(searchResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "장소 수정", description = "[ADMIN, 장소 최초 생성자] 특정 장소를 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/{locationId}")
    public ResponseEntity updateLocation(@PathVariable("locationId") Long locationId, @RequestBody LocationUpdateDTO dto) {
        String username =
                SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        LocationResponseDTO location = locationService.updateLocation(locationId, dto, username);

        return new ResponseEntity(location, HttpStatus.OK);
    }
}
