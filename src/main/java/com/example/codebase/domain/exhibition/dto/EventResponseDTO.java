package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.Event;
import com.example.codebase.domain.exhibition.entity.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {

    private Long id;

    private String title;

    private String author;

    private ExhibitionMediaResponseDTO thumbnail;

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
                .thumbnail(ExhibitionMediaResponseDTO.from(event.getEventMedias().get(0)))
                .eventType(event.getType())
                .location(event.getLocation().getAddress())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .createdTime(event.getCreatedTime())
                .updatedTime(event.getUpdatedTime())
                .build();
    }
}
