package com.example.codebase.domain.exhibition.service;

import com.example.codebase.domain.exhibition.entity.Event;
import com.example.codebase.domain.exhibition.entity.EventMedia;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.EventRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final ExhibitionRepository exhibitionRepository;

    private final EventRepository eventRepository;


    @Autowired
    public EventService(ExhibitionRepository exhibitionRepository, EventRepository eventRepository) {
        this.exhibitionRepository = exhibitionRepository;
        this.eventRepository = eventRepository;
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

            if(event == null) continue;

            eventMedias = EventMedia.of(exhibition, event);
            event.setEventMedias(eventMedias);
            events.add(event);
        }
        return events;
    }


}
