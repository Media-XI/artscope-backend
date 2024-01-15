package com.example.codebase.domain.location.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.location.dto.*;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final MemberRepository memberRepository;

    @Autowired
    public LocationService(
            LocationRepository locationRepository,
            MemberRepository memberRepository) {
        this.locationRepository = locationRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public LocationResponseDTO createLocation(LocationCreateDTO dto, String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        if (!member.isSubmitedRoleInformation() && !SecurityUtil.isAdmin()) {
            throw new RuntimeException("추가정보 입력한 사용자만 장소를 생성할 수 있습니다.");
        }

        Location location = findOrCreateLocation(dto, member);
        locationRepository.save(location);

        return LocationResponseDTO.from(location);
    }

    private Location findOrCreateLocation(LocationCreateDTO dto, Member member) {
        List<Location> locations = locationRepository.findByGpsXAndGpsYOrAddress(String.valueOf(dto.getLatitude()), String.valueOf(dto.getLongitude()), dto.getName());

        if (locations.isEmpty()) {
            Location newLocation = Location.of(dto, member);
            locationRepository.save(newLocation);
            return newLocation;
        } else {
            return locations.get(0);
        }

    }

    @Transactional(readOnly = true)
    public LocationResponseDTO getLocation(Long locationId) {
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        return LocationResponseDTO.from(location);
    }

    @Transactional
    public void deleteLocation(Long locationId, String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        Location location =
                locationRepository.findById(locationId).orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!location.equalsUsername(username)) {
            throw new RuntimeException("장소 작성자만 삭제할 수 있습니다.");
        }

        if (location.hasEvents()) {
            throw new RuntimeException("장소에 등록된 이벤트가 있습니다.");
        }

        locationRepository.delete(location);

    }

    @Transactional
    public LocationResponseDTO updateLocation(Long locationId, LocationUpdateDTO dto, String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        Location location =
                locationRepository.findById(locationId).orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        if (!location.equalsUsername(username)) {
            throw new RuntimeException("장소 작성자만 수정할 수 있습니다.");
        }

        location.update(dto);

        locationRepository.save(location);

        return LocationResponseDTO.from(location);
    }

    @Transactional(readOnly = true)
    public LocationsSearchResponseDTO searchLocation(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Location> locations = locationRepository.findByKeyword(keyword, pageRequest);

        PageInfo pageInfo =
                PageInfo.of(page, size, locations.getTotalPages(), locations.getTotalElements());

        List<LocationSearchResponseDTO> locationsResponseDTOs =
                locations.stream().map(LocationSearchResponseDTO::from).collect(Collectors.toList());

        return LocationsSearchResponseDTO.of(locationsResponseDTOs, pageInfo);
    }

}
