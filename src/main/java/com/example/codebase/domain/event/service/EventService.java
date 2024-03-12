package com.example.codebase.domain.event.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.event.dto.*;
import com.example.codebase.domain.event.entity.*;
import com.example.codebase.domain.event.repository.EventRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        Location location = findByLocationId(dto.getLocationId());

        Event event = Event.of(dto, member, location);

        EventMedia thumbnail = EventMedia.of(dto.getThumbnail(), event);
        event.addEventMedia(thumbnail);

        for (EventMediaCreateDTO mediaCreateDTO : dto.getMedias()) {
            EventMedia media = EventMedia.of(mediaCreateDTO, event);
            event.addEventMedia(media);
        }

        location.addEvent(event);
        eventRepository.save(event);

        return EventDetailResponseDTO.from(event);
    }

    private Location findByLocationId(Long locationId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException("장소를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public EventsResponseDTO getEvents(EventSearchDTO eventSearchDTO, int page, int size, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        SearchEventType searchEventType = SearchEventType.create(eventSearchDTO.getEventType());
        EventType eventType = EventType.create(searchEventType.name());

        Page<Event> events = eventRepository.findEventsByEventType(eventSearchDTO.getStartDate(), eventSearchDTO.getEndDate(), eventType, pageRequest);

        PageInfo pageInfo = PageInfo.of(page, size, events.getTotalPages(), events.getTotalElements());

        List<EventResponseDTO> dtos = events.getContent().stream()
                .map(EventResponseDTO::from)
                .toList();

        return EventsResponseDTO.of(dtos, pageInfo);
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

        if (!event.equalUsername(member.getUsername())) {
            throw new RuntimeException("해당 이벤트의 작성자가 아닙니다");
        }

        Location location = Optional.ofNullable(dto.getLocationId())
                .map(this::findByLocationId)
                .orElse(null);

        event.update(dto, location);

        return EventDetailResponseDTO.from(event);
    }

    @Transactional
    public void deleteEvent(Long eventId, String username) {
        Event event = findEventById(eventId);

        if (!event.equalUsername(username)) {
            throw new RuntimeException("해당 이벤트의 작성자가 아닙니다");
        }

        event.delete();
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public EventsResponseDTO getEventsByMember(Member member, PageRequest pageRequest) {
        Page<Event> events = eventRepository.findEventsByMember(member, pageRequest);

        PageInfo pageInfo = PageInfo.of(events.getNumber(), events.getSize(), events.getTotalPages(), events.getTotalElements());

        List<EventResponseDTO> dtos = events.getContent().stream()
                .map(EventResponseDTO::from)
                .toList();

        return EventsResponseDTO.of(dtos, pageInfo);
    }

}
