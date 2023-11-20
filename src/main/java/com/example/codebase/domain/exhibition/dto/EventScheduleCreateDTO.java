package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class EventScheduleCreateDTO {

    @NotBlank(message = "시작시간은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

    @NotBlank(message = "장소는 필수입니다.")
    private Long locationId;

    private String detailLocation;

    private List<ParticipantInformationDTO> participants;

    public void checkTimeValidity() {
        if (this.startDateTime.equals(this.endDateTime)) {
            throw new RuntimeException("시작시간과 종료시간이 같을 수 없습니다.");
        }
    }
}
