package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventScheduleCreateDTO {

    @NotNull(message = "시작시간은 필수입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDateTime;

    @NotNull(message = "장소는 필수입니다.")
    private Long locationId;

    private String detailLocation;

    private List<ParticipantInformationDTO> participants;

    public void checkTimeValidity() {
        if (this.endDateTime == null) {
            return;
        }
        if (this.startDateTime.equals(this.endDateTime)) {
            throw new RuntimeException("시작시간과 종료시간이 같을 수 없습니다.");
        }
        if (this.startDateTime.isAfter(this.endDateTime)) {
            throw new RuntimeException("시작시간이 종료시간보다 늦을 수 없습니다.");
        }
    }
}
