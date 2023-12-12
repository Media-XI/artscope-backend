package com.example.codebase.domain.exhibition.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.exhibition.dto.*;
import com.example.codebase.domain.exhibition.entity.*;
import com.example.codebase.domain.exhibition.repository.EventRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final ExhibitionRepository exhibitionRepository;

    private final EventRepository eventRepository;

    private final MemberRepository memberRepository;

    private final LocationRepository locationRepository;


    @Autowired
    public EventService(ExhibitionRepository exhibitionRepository, EventRepository eventRepository, MemberRepository memberRepository, LocationRepository locationRepository) {
        this.exhibitionRepository = exhibitionRepository;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public void moveEventSchedule() {
        List<Exhibition> exhibitions = exhibitionRepository.findAllExhibitionsIgnoreEnabled();

        List<Event> events = transformExhibitionsToEvents(exhibitions);

        eventRepository.saveAll(events);
    }

    private List<Event> transformExhibitionsToEvents(List<Exhibition> exhibitions) {
        List<Event> events = new ArrayList<>();

        for (Exhibition exhibition : exhibitions) {
            List<EventMedia> eventMedias;
            Event event = Event.from(exhibition);

            if (event == null) continue;

            eventMedias = EventMedia.of(exhibition, event);
            event.setEventMedias(eventMedias);
            events.add(event);
        }
        return events;
    }

    @Transactional
    public EventDetailResponseDTO createEvent(EventCreateDTO dto, String username) {
        dto.validateDates();

        Member member = findMemberByUserName(username);

        if (!member.isSubmitedRoleInformation()) {
            throw new RuntimeException("추가정보 입력한 사용자만 이벤트를 생성할 수 있습니다.");
        }

        Location location = findLocationByLocationId(dto.getLocationId());

        Event event = Event.of(dto, member, location);

        EventMedia thumbnail = EventMedia.of(dto.getThumbnail(), event);
        event.addEventMedia(thumbnail);

        for (ExhibitionMediaCreateDTO mediaCreateDTO : dto.getMedias()) {
            EventMedia media = EventMedia.of(mediaCreateDTO, event);
            event.addEventMedia(media);
        }

        eventRepository.save(event);

        return EventDetailResponseDTO.from(event);
    }

    private Member findMemberByUserName(String username) {
        return memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);
    }

    private Location findLocationByLocationId(Long locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
    }

    public EventPageInfoResponseDTO getEvents(EventSearchDTO eventSearchDTO, int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        SearchEventType searchEventType = SearchEventType.create(eventSearchDTO.getEventType());
        EventType eventType = EventType.create(searchEventType.name());

        Page<Event> events = findEventsBySearchCondition(eventSearchDTO, pageRequest, eventType);

        PageInfo pageInfo = PageInfo.of( page,size, events.getTotalPages(), events.getTotalElements());

        List<EventResponseDTO> dtos = events.getContent().stream()
                .map(EventResponseDTO::from)
                .toList();

        return EventPageInfoResponseDTO.of(dtos, pageInfo);
    }

    private Page<Event> findEventsBySearchCondition(EventSearchDTO eventSearchDTO, PageRequest pageRequest, EventType eventType) {
        if (eventType == null) {
            return eventRepository.findAllBySearchCondition(eventSearchDTO.getStartDate(),eventSearchDTO.getEndDate(), pageRequest);
        }
        return eventRepository.findAllBySearchConditionAndEventType(eventSearchDTO.getStartDate(),eventSearchDTO.getEndDate(), eventType, pageRequest);
    }

    @Transactional(readOnly = true)
    public EventDetailResponseDTO getEventDetail(Long eventId) {
        Event event = findEventById(eventId);
        return EventDetailResponseDTO.from(event);
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));
    }

    @Transactional
    public EventDetailResponseDTO updateEvent(Long eventId, EventUpdateDTO dto, String username) {
        Member member = findMemberByUserName(username);

        Event event = findEventById(eventId);

        Location location = null;
        if(dto.getLocationId() != null){
            location = findLocationByLocationId(dto.getLocationId());
        }

        if (!SecurityUtil.isAdmin() && !event.equalUsername(username)) {
            throw new RuntimeException("해당 이벤트의 작성자가 아닙니다");
        }

        event.update(dto, location);

        return EventDetailResponseDTO.from(event);
    }

    @Transactional
    public void deleteEvent(Long eventId, String username) {
        Event event = findEventById(eventId);

        if (!SecurityUtil.isAdmin() && !event.equalUsername(username)) {
            throw new RuntimeException("해당 이벤트의 작성자가 아닙니다");
        }

       eventRepository.delete(event);
    }


}
