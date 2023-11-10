package com.example.codebase.domain.location.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.dto.LocationResponseDTO;
import com.example.codebase.domain.location.dto.LocationSearchResponseDTO;
import com.example.codebase.domain.location.dto.LocationsSearchResponseDTO;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

  private final LocationRepository locationRepository;

  private final ExhibitionRepository exhibitionRepository;

  private final MemberRepository memberRepository;

  @Autowired
  public LocationService(
      LocationRepository locationRepository,
      MemberRepository memberRepository,
      ExhibitionRepository exhibitionRepository) {
    this.locationRepository = locationRepository;
    this.memberRepository = memberRepository;
    this.exhibitionRepository = exhibitionRepository;
  }

  @Transactional
  public LocationResponseDTO createLocation(LocationCreateDTO dto, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    Location location = Location.of(dto);

    locationRepository.save(location);

    return LocationResponseDTO.of(location);
  }

  @Transactional(readOnly = true)
  public LocationResponseDTO getLocation(Long locationId) {
    Location location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

    return LocationResponseDTO.of(location);

    // TODO : 스케쥴, 이벤트 관련 반환 로직 구현 필요
  }

  @Transactional
  public void deleteLocation(Long locationId, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    throw new RuntimeException("deleteLocation 구현 안됨"); // TODO: 현재 장소에 대한 삭제 로직 구현 필요
  }

  @Transactional(readOnly = true)
  public LocationsSearchResponseDTO findLocationByKeyword(String keyword, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size);

    Page<Location> locations = locationRepository.findByKeyword(keyword, pageRequest);
    PageInfo pageInfo =
        PageInfo.of(page, size, locations.getTotalPages(), locations.getTotalElements());

    List<LocationSearchResponseDTO> locationsResponseDTOs =
        locations.stream().map(LocationSearchResponseDTO::from).collect(Collectors.toList());

    return LocationsSearchResponseDTO.of(locationsResponseDTOs, pageInfo);
  }
}
