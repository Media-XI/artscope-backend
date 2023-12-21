package com.example.codebase.domain.Event.dto;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.Event.document.EventDocument;
import com.example.codebase.domain.Event.entity.Event;
import com.example.codebase.domain.Event.entity.EventMedia;
import com.example.codebase.domain.Event.entity.EventType;
import com.example.codebase.domain.agora.dto.AgoraMediaResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

    private Long id;

    private String title;

    private String author;

    private EventMediaResponseDTO thumbnail;

    private EventType eventType;

    private String location;

    private String detailLocation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;


    public static EventResponseDTO from(Event event) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .author(event.getMember().getName())
                .thumbnail(EventMediaResponseDTO.from(event.getEventMedias().get(0)))
                .eventType(event.getType())
                .location(event.getLocation().getAddress())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .createdTime(event.getCreatedTime())
                .updatedTime(event.getUpdatedTime())
                .build();
    }

    public static EventResponseDTO from (EventDocument eventDocument){

        EventMediaResponseDTO media = null;
        if (eventDocument.getMediaUrl() != null) {
            media = EventMediaResponseDTO.from(eventDocument.getMediaUrl());
        }


        return EventResponseDTO.builder()
                .id(eventDocument.getId())
                .title(eventDocument.getTitle())
                .thumbnail(media)
                .createdTime(eventDocument.getCreatedTime())
                .updatedTime(eventDocument.getUpdatedTime())
                .build();
    }

}
