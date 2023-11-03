package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventSchedule;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime eventDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalDateTime endTime;

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
                .eventDate(eventSchedule.getEventDate())
                .startTime(eventSchedule.getStartTime())
                .endTime(eventSchedule.getEndTime())
                .createdTime(eventSchedule.getCreatedTime())
                .updatedTime(eventSchedule.getUpdatedTime())
                .build();
    }
}
