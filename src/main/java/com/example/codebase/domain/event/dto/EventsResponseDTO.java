package com.example.codebase.domain.event.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class EventsResponseDTO {

    List<EventResponseDTO> events;

    PageInfo pageInfo;

    public static EventsResponseDTO of(
        List<EventResponseDTO> dtos, PageInfo pageInfo) {
        EventsResponseDTO responseDTO = new EventsResponseDTO();
        responseDTO.events = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }

    public void addEvent(EventResponseDTO from) {
        this.events.add(from);
    }
}
