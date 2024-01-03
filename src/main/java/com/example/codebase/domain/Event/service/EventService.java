package com.example.codebase.domain.Event.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.Event.dto.*;
import com.example.codebase.domain.Event.entity.*;
import com.example.codebase.domain.Event.repository.EventRepository;
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

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    private final MemberRepository memberRepository;

    private final LocationRepository locationRepository;


    @Autowired
    public EventService(EventRepository eventRepository, MemberRepository memberRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional
    public EventDetailResponseDTO createEvent(EventCreateDTO dto, Member member) {
        Location location = findLocationByLocationId(dto.getLocationId());

        Event event = Event.of(dto, member, location);

        EventMedia thumbnail = EventMedia.of(dto.getThumbnail(), event);
        event.addEventMedia(thumbnail);

        for (EventMediaCreateDTO mediaCreateDTO : dto.getMedias()) {
            EventMedia media = EventMedia.of(mediaCreateDTO, event);
            event.addEventMedia(media);
        }

        eventRepository.save(event);

        return EventDetailResponseDTO.from(event);
    }

    public Member findMemberByUserName(String username) {
        return memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);
    }

    private Location findLocationByLocationId(Long locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
    }

    public EventsResponseDTO getEvents(EventSearchDTO eventSearchDTO, int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        SearchEventType searchEventType = SearchEventType.create(eventSearchDTO.getEventType());
        EventType eventType = EventType.create(searchEventType.name());

        Page<Event> events = findEventsBySearchCondition(eventSearchDTO, pageRequest, eventType);

        PageInfo pageInfo = PageInfo.of(page, size, events.getTotalPages(), events.getTotalElements());

        List<EventResponseDTO> dtos = events.getContent().stream()
                .map(EventResponseDTO::from)
                .toList();

        return EventsResponseDTO.of(dtos, pageInfo);
    }

    private Page<Event> findEventsBySearchCondition(EventSearchDTO eventSearchDTO, PageRequest pageRequest, EventType eventType) {
        if (eventType == null) {
            return eventRepository.findAllBySearchCondition(eventSearchDTO.getStartDate(), eventSearchDTO.getEndDate(), pageRequest);
        }
        return eventRepository.findAllBySearchConditionAndEventType(eventSearchDTO.getStartDate(), eventSearchDTO.getEndDate(), eventType, pageRequest);
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
        Member member = memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        Event event = findEventById(eventId);

        if (!SecurityUtil.isAdmin() && !event.equalUsername(username)) {
            throw new RuntimeException("해당 이벤트의 작성자가 아닙니다");
        }

        if (dto.getLocationId() != null) {
            Location location = findLocationByLocationId(dto.getLocationId());
            event.update(location);
        }

        event.update(dto);

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
