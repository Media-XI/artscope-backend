package com.example.codebase.domain.location.service;

import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.dto.CreateLocationDTO;
import com.example.codebase.domain.location.dto.ResponseLocationDTO;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
  public ResponseLocationDTO createLocation(CreateLocationDTO dto, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    Location location = Location.of(dto);

    locationRepository.save(location);

    return ResponseLocationDTO.of(location);
  }

  @Transactional(readOnly = true)
  public ResponseLocationDTO getLocation(
      Long locationId, int page, int size, String sortDirection) {
    Location location =
        locationRepository
            .findById(locationId)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
    PageRequest pageRequest = PageRequest.of(page, size, sort);
    Page<Exhibition> exhibitions;
    //
    //    PageInfo pageInfo =
    //        PageInfo.of(page, size, exhibitions.getTotalPages(), exhibitions.getTotalElements());

    return null;

    //    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
    //    PageRequest pageRequest = PageRequest.of(page, size, sort);
    //    Page<Exhibition> exhibitions;
    //
    //    exhibitions = exhibitionRepository.findByLocation(location.getId(), pageRequest);
    //
    //    PageInfo pageInfo =
    //        PageInfo.of(page, size, exhibitions.getTotalPages(), exhibitions.getTotalElements());
    //
    //    List<ResponseExhibitionDTO> exhibitionDtos =
    //        exhibitions.getContent().stream()
    //            .map(ResponseExhibitionDTO::from)
    //            .collect(Collectors.toList());
    //
    //    ResponseExhibitionPageInfoDTO responseExhibitionPageInfoDto =
    //        ResponseExhibitionPageInfoDTO.of(exhibitionDtos, pageInfo);
    //    // return ResponseLocationDTO.of(location, responseExhibitionPageInfoDto);
    //    return ResponseLocationDTO.of(location);
  }

  @Transactional
  public void deleteLocation(Long locationId, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    throw new RuntimeException("deleteLocation 구현 안됨");
  }
}
