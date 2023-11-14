package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.location.dto.LocationResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionDetailResponseDTO {

    private Long id;

    private String title;

    private String author;

    private String description;

    private ExhibitionMediaResponseDTO thumbnail;

    private List<ExhibitionMediaResponseDTO> medias;

    private String link;

    private EventType eventType;

    private List<EventScheduleResponseDTO> eventSchedules;

    private String detailLocation;

    private int price;

    private LocationResponseDTO location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static ExhibitionDetailResponseDTO from(Exhibition exhibition) {
        List<ExhibitionMedia> medias = exhibition.getExhibitionMedias();

        ExhibitionMediaResponseDTO thumbnail =
                medias.stream().findFirst().map(ExhibitionMediaResponseDTO::from).orElse(null);

        // 미디어
        List<ExhibitionMediaResponseDTO> exhibitionMediaResponseDTOS =
                medias.stream().skip(1).map(ExhibitionMediaResponseDTO::from).collect(Collectors.toList());

        List<EventScheduleResponseDTO> eventScheduleDTOS =
                exhibition.getEventSchedules().stream()
                        .map(EventScheduleResponseDTO::from)
                        .collect(Collectors.toList());

        // Location
        LocationResponseDTO locationDTO =
                LocationResponseDTO.from(exhibition.getEventSchedules().get(0));

        return ExhibitionDetailResponseDTO.builder()
                .id(exhibition.getId())
                .title(exhibition.getTitle())
                .author(exhibition.getMember().getName())
                .description(exhibition.getDescription())
                .thumbnail(thumbnail)
                .medias(exhibitionMediaResponseDTOS)
                .eventSchedules(eventScheduleDTOS)
                .link(exhibition.getLink())
                .eventType(exhibition.getType())
                .detailLocation(exhibition.getEventSchedules().get(0).getDetailLocation())
                .price(exhibition.getPrice())
                .location(locationDTO)
                .createdTime(exhibition.getCreatedTime())
                .updatedTime(exhibition.getUpdatedTime())
                .build();

    }
}
