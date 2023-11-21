package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventScheduleResponseDTO {

    private Long id;

    private String locationName;

    private String locationAddress;

    private String detailLocation;

    private List<ParticipantInformationResponseDTO> participants;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static EventScheduleResponseDTO from(EventSchedule eventSchedule) {
        List<ParticipantInformationResponseDTO> participantInformationResponseDTO =
                eventSchedule.getExhibitionParticipants().stream()
                        .map(ParticipantInformationResponseDTO::from)
                        .collect(Collectors.toList());
        return EventScheduleResponseDTO.builder()
                .id(eventSchedule.getId())
                .locationName(eventSchedule.getLocation().getName())
                .locationAddress(eventSchedule.getLocation().getAddress())
                .detailLocation(eventSchedule.getDetailLocation())
                .participants(participantInformationResponseDTO)
                .startDateTime(eventSchedule.getStartDateTime())
                .endDateTime(eventSchedule.getEndDateTime())
                .createdTime(eventSchedule.getCreatedTime())
                .updatedTime(eventSchedule.getUpdatedTime())
                .build();
    }
}
