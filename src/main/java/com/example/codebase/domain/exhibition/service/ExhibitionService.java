package com.example.codebase.domain.exhibition.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.exhibition.dto.CreateEventScheduleDTO;
import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
import com.example.codebase.domain.exhibition.dto.ParticipantInformationDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionIntroduceDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionPageInfoDTO;
import com.example.codebase.domain.exhibition.dto.SearchExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.UpdateExhibitionDTO;
import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.exhibition.entity.ExhibitionParticipant;
import com.example.codebase.domain.exhibition.entity.SearchEventType;
import com.example.codebase.domain.exhibition.repository.EventScheduleRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionParticipantRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExhibitionService {

  private final ExhibitionRepository exhibitionRepository;

  private final ExhibitionParticipantRepository exhibitionParticipantRepository;

  private final EventScheduleRepository eventScheduleRepository;

  private final MemberRepository memberRepository;

  private final LocationRepository locationRepository;

  @Autowired
  public ExhibitionService(
      ExhibitionRepository exhibitionRepository,
      ExhibitionParticipantRepository exhibitionParticipantRepository,
      EventScheduleRepository eventScheduleRepository,
      MemberRepository memberRepository,
      LocationRepository locationRepository) {
    this.exhibitionRepository = exhibitionRepository;
    this.exhibitionParticipantRepository = exhibitionParticipantRepository;
    this.eventScheduleRepository = eventScheduleRepository;
    this.memberRepository = memberRepository;
    this.locationRepository = locationRepository;
  }

  @Transactional
  public ResponseExhibitionDTO createExhibition(CreateExhibitionDTO dto, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    // 이벤트 생성
    Exhibition exhibition = Exhibition.of(dto, member);

    // 썸네일 추가
    ExhibitionMedia thumbnail = ExhibitionMedia.of(dto.getThumbnail(), exhibition);
    exhibition.addExhibitionMedia(thumbnail); // 제일 첫번째는 썸네일로

    // 미디어 추가
    for (ExhibitionMediaCreateDTO mediaCreateDTO : dto.getMedias()) {
      ExhibitionMedia media = ExhibitionMedia.of(mediaCreateDTO, exhibition);
      exhibition.addExhibitionMedia(media);
    }
    // 이벤트 저장
    exhibitionRepository.save(exhibition);

    if (dto.getSchedule().size() < 1) {
      throw new RuntimeException("스케쥴을 등록해주세요.");
    }

    CreateEventScheduleDTO scheduleDTO = dto.getSchedule().get(0);
    // 장소 찾기
    Location location =
        locationRepository
            .findById(scheduleDTO.getLocationId())
            .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));

    // 이벤트 스케쥴 등록
    for (CreateEventScheduleDTO schedule : dto.getSchedule()) {
      createEventSchedule(schedule, location, exhibition);
    }

    return ResponseExhibitionDTO.from(exhibition);
  }

  @Transactional
  public void createEventSchedule(
      Long exhibitionId, CreateEventScheduleDTO scheduleDTO, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    Exhibition exhibition =
        exhibitionRepository.findById(exhibitionId).orElseThrow(RuntimeException::new);

    Location location =
        locationRepository
            .findById(scheduleDTO.getLocationId())
            .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));

    // 이벤트 스케쥴 최초 등록
    createEventSchedule(scheduleDTO, location, exhibition);
  }

  public void createEventSchedule(
      CreateEventScheduleDTO schedule, Location location, Exhibition exhibition) {
    schedule.checkTimeValidity();

    EventSchedule eventSchedule = EventSchedule.from(schedule);
    eventSchedule.setLocation(location);
    eventSchedule.setEvent(exhibition);

    List<ParticipantInformationDTO> participants =
        schedule.getParticipants() != null ? schedule.getParticipants() : Collections.emptyList();
    for (ParticipantInformationDTO participant : participants) {
      ExhibitionParticipant exhibitionParticipant = new ExhibitionParticipant();

      if (participant.getUsername() != null) {
        Member participantMember =
            memberRepository
                .findByUsername(participant.getUsername())
                .orElseThrow(NotFoundMemberException::new);
        exhibitionParticipant.setMember(participantMember);
      }
      exhibitionParticipant.setEventSchedule(eventSchedule);
      exhibitionParticipantRepository.save(exhibitionParticipant);
    }

    eventScheduleRepository.save(eventSchedule);
  }

  @Transactional(readOnly = true)
  public ResponseExhibitionPageInfoDTO getAllExhibition(
      SearchExhibitionDTO searchExhibitionDTO, int page, int size, String sortDirection) {

    searchExhibitionDTO.repeatTimeValidity();
    SearchEventType searchEventType = SearchEventType.create(searchExhibitionDTO.getEventType());

    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
    PageRequest pageRequest = PageRequest.of(page, size, sort);

    Page<EventSchedule> eventSchedules;
    if (searchEventType == SearchEventType.ALL) {
      eventSchedules =
          eventScheduleRepository.findByStartAndEndDate(
              searchExhibitionDTO.getStartDate(), searchExhibitionDTO.getEndDate(), pageRequest);
    } else {
      eventSchedules =
          eventScheduleRepository.findByStartAndEndDate(
              searchExhibitionDTO.getStartDate(),
              searchExhibitionDTO.getEndDate(),
              EventType.valueOf(searchEventType.name()),
              pageRequest);
    }

    PageInfo pageInfo =
        PageInfo.of(page, size, eventSchedules.getTotalPages(), eventSchedules.getTotalElements());

    List<ResponseExhibitionDTO> dtos = new ArrayList<>();
    for (int i = 0; i < eventSchedules.getContent().size(); i++) {
      Exhibition exhibition =
          exhibitionRepository
              .findById(eventSchedules.getContent().get(i).getExhibition().getId())
              .orElseThrow();
      ResponseExhibitionDTO dto = ResponseExhibitionDTO.from(exhibition);
      dtos.add(dto);
    }
    return ResponseExhibitionPageInfoDTO.of(dtos, pageInfo);
  }

  @Transactional(readOnly = true)
  public ResponseExhibitionIntroduceDTO getExhibitionDetail(Long exhibitionId) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

    return ResponseExhibitionIntroduceDTO.from(exhibition);
  }

  @Transactional
  public ResponseExhibitionIntroduceDTO updateExhibition(
      Long exhibitionId, UpdateExhibitionDTO dto, String username) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    if (!member.equals(exhibition.getMember())) {
      throw new RuntimeException("이벤트의 작성자가 아닙니다.");
    }

    exhibition.update(dto);

    return ResponseExhibitionIntroduceDTO.from(exhibition);
  }

  @Transactional
  public void deleteExhibition(Long exhibitionId, String username) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);
    if (!member.equals(exhibition.getMember())) {
      throw new RuntimeException("이벤트를 삭제할 권한이 없습니다.");
    }

    exhibition.delete();
    eventScheduleRepository.deleteAll(exhibition.getEventSchedules());
  }

  @Transactional
  public void deleteAllEventSchedules(Long exhibitionId, String username) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    if (!member.equals(exhibition.getMember())) {
      throw new RuntimeException("해당 이벤트의 생성자가 아닙니다.");
    }

    List<EventSchedule> eventSchedules = exhibition.getEventSchedules();
    exhibition.deleteEventSchedules();

    eventScheduleRepository.deleteAll(eventSchedules);
  }

  public void deleteEventSchedule(Long exhibitionId, Long eventScheduleId, String username) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

    Member member =
        memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

    if (!member.equals(exhibition.getMember())) {
      throw new RuntimeException("이벤트 일정을 삭제할 권한이 없습니다.");
    }
    exhibition.deleteEventSchedules();
    eventScheduleRepository.deleteById(eventScheduleId);
  }
}
