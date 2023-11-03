package com.example.codebase.domain.location.service;

import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.dto.LocationResponseDTO;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public LocationResponseDTO getLocation(
            Long locationId, int page, int size, String sortDirection) {
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 장소입니다."));

        throw new RuntimeException("getLocation 구현 안됨"); // TODO: 현재 장소에 대한 정보 구현 필요
        //
        //    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        //    PageRequest pageRequest = PageRequest.of(page, size, sort);
        //    Page<Exhibition> exhibitions;
        //
        //        PageInfo pageInfo =
        //            PageInfo.of(page, size, exhibitions.getTotalPages(),
        // exhibitions.getTotalElements());
        //

    }

    @Transactional
    public void deleteLocation(Long locationId, String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        throw new RuntimeException("deleteLocation 구현 안됨"); // TODO: 현재 장소에 대한 삭제 로직 구현 필요
    }
}
