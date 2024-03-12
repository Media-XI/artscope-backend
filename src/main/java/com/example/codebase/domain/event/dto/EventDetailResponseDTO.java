package com.example.codebase.domain.event.dto;

import com.example.codebase.domain.event.entity.Event;
import com.example.codebase.domain.event.entity.EventMedia;
import com.example.codebase.domain.event.entity.EventType;
import com.example.codebase.domain.location.dto.LocationResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EventDetailResponseDTO {

    private Long id;

    private String title;

    private String authorName;

    private String authorUserName;

    private String authorProfileImage;

    private String description;

    private String detailLocation;

    private String price;

    private String link;

    private EventType eventType;

    private EventMediaResponseDTO thumbnail;

    private List<EventMediaResponseDTO> medias;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String detailedSchedule;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    private LocationResponseDTO location;

    public static EventDetailResponseDTO from(Event event) {
        List<EventMedia> medias = event.getEventMedias();

        EventMediaResponseDTO thumbnail =
                medias.stream().findFirst().map(EventMediaResponseDTO::from).orElse(null);

        List<EventMediaResponseDTO> eventMediaResponseDTOS =
                medias.stream().skip(1).map(EventMediaResponseDTO::from).collect(Collectors.toList());

        LocationResponseDTO locationResponseDTO = LocationResponseDTO.from(event.getLocation());

        return EventDetailResponseDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .authorName(event.getMember().getName())
                .authorUserName(event.getMember().getUsername())
                .description(event.getDescription())
                .thumbnail(thumbnail)
                .medias(eventMediaResponseDTOS)
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .detailedSchedule(event.getDetailedSchedule())
                .eventType(event.getType())
                .price(event.getPrice())
                .link(event.getLink())
                .detailLocation(event.getDetailLocation())
                .price(event.getPrice())
                .location(locationResponseDTO)
                .createdTime(event.getCreatedTime())
                .updatedTime(event.getUpdatedTime())
                .build();
    }
}
