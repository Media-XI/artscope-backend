package com.example.codebase.domain.exhibition.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.exhibition.dto.*;
import com.example.codebase.domain.exhibition.entity.*;
import com.example.codebase.domain.exhibition.repository.EventScheduleRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionParticipantRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import com.example.codebase.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public ExhibitionDetailResponseDTO createExhibition(ExhbitionCreateDTO dto, String username) {
        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        if (!member.isSubmitedRoleInformation()) {
            throw new RuntimeException("추가정보 입력한 사용자만 이벤트를 생성할 수 있습니다.");
        }

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

        EventScheduleCreateDTO scheduleDTO = dto.getSchedule().get(0);
        // 장소 찾기
        Location location =
                locationRepository
                        .findById(scheduleDTO.getLocationId())
                        .orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));

        // 이벤트 스케쥴 등록
        for (EventScheduleCreateDTO schedule : dto.getSchedule()) {
            createEventSchedule(schedule, location, exhibition);
        }

        return ExhibitionDetailResponseDTO.from(exhibition);
    }

    @Transactional
    public void createEventSchedule(
            Long exhibitionId, EventScheduleCreateDTO scheduleDTO, String username) {
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
            EventScheduleCreateDTO schedule, Location location, Exhibition exhibition) {
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
    public ExhibitionPageInfoResponseDTO getAllExhibition(
            ExhibitionSearchDTO exhibitionSearchDTO, int page, int size, String sortDirection) {

        exhibitionSearchDTO.repeatTimeValidity();

        Sort sort = Sort.by(Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        SearchEventType searchEventType = SearchEventType.create(exhibitionSearchDTO.getEventType());
        EventType eventType = EventType.create(searchEventType.name());

        Page<ExhibitionWithEventSchedule> exhibitions =
                findExhibitionsWithEventSchedules(eventType, exhibitionSearchDTO, pageRequest);

        PageInfo pageInfo =
                PageInfo.of(page, size, exhibitions.getTotalPages(), exhibitions.getTotalElements());

        List<ExhibitionResponseDTO> dtos =
                exhibitions.getContent().stream()
                        .map(ExhibitionResponseDTO::from)
                        .collect(Collectors.toList());

        return ExhibitionPageInfoResponseDTO.of(dtos, pageInfo);
    }

    private Page<ExhibitionWithEventSchedule> findExhibitionsWithEventSchedules(
            EventType eventType, ExhibitionSearchDTO exhibitionSearchDTO, PageRequest pageRequest) {
        if (eventType == null) {
            return exhibitionRepository.findExhibitionsWithEventSchedules(
                    exhibitionSearchDTO.getStartDateTime(),
                    exhibitionSearchDTO.getEndDateTime(),
                    pageRequest);
        }
        return exhibitionRepository.findExhibitionsWithEventSchedules(
                exhibitionSearchDTO.getStartDateTime(),
                exhibitionSearchDTO.getEndDateTime(),
                eventType,
                pageRequest);
    }

    @Transactional(readOnly = true)
    public ExhibitionDetailResponseDTO getExhibitionDetail(Long exhibitionId) {
        Exhibition exhibition =
                exhibitionRepository
                        .findById(exhibitionId)
                        .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        return ExhibitionDetailResponseDTO.from(exhibition);
    }

    @Transactional
    public ExhibitionDetailResponseDTO updateExhibition(
            Long exhibitionId, ExhibitionUpdateDTO dto, String username) {
        Exhibition exhibition =
                exhibitionRepository
                        .findById(exhibitionId)
                        .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        if (!SecurityUtil.isAdmin() && !member.equals(exhibition.getMember())) {
            throw new RuntimeException("이벤트의 작성자가 아닙니다.");
        }

        exhibition.update(dto);

        return ExhibitionDetailResponseDTO.from(exhibition);
    }

    @Transactional
    public void deleteExhibition(Long exhibitionId, String username) {
        Exhibition exhibition =
                exhibitionRepository
                        .findById(exhibitionId)
                        .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        if (!SecurityUtil.isAdmin() && !exhibition.equalUsername(username)) {
            throw new RuntimeException("해당 이벤트의 작성자가 아닙니다");
        }
        exhibition.delete();
        eventScheduleRepository.deleteAll(exhibition.getEventSchedules());
    }

    @Transactional
    public void deleteEventSchedule(Long exhibitionId, Long eventScheduleId, String username) {
        Exhibition exhibition =
                exhibitionRepository
                        .findById(exhibitionId)
                        .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        EventSchedule eventSchedule =
                eventScheduleRepository.findById(eventScheduleId).orElseThrow(RuntimeException::new);

        if (!exhibition.getEventSchedules().contains(eventSchedule)) {
            throw new RuntimeException("해당 이벤트의 일정이 아닙니다.");
        }

        if (!SecurityUtil.isAdmin() && !exhibition.equalUsername(username)) {
            throw new RuntimeException("해당 이벤트의 생성자가 아닙니다.");
        }

        eventSchedule.delete();
        eventScheduleRepository.delete(eventSchedule);
    }

    @Transactional
    public void deleteAllEventSchedules(Long exhibitionId, String username) {
        Exhibition exhibition =
                exhibitionRepository
                        .findById(exhibitionId)
                        .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        Member member =
                memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        if (!SecurityUtil.isAdmin() && (!member.equals(exhibition.getMember()))) {
            throw new RuntimeException("이벤트 일정을 삭제할 권한이 없습니다.");
        }

        List<EventSchedule> eventSchedules = exhibition.getEventSchedules();
        for (EventSchedule eventSchedule : eventSchedules) {
            eventSchedule.delete();
        }
        eventScheduleRepository.deleteAll(eventSchedules);
    }
}
