package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class ExhibitionSearchDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotBlank(message = "이벤트 타입은 필수입니다.")
    private String eventType;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Builder
    @JsonIncludeProperties({"startDate", "endDate", "eventType"})
    public ExhibitionSearchDTO(LocalDate startDate, LocalDate endDate, String eventType) {
        setStartDate(startDate);
        setEndDate(endDate);
        this.eventType = eventType;
    }

    private void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            startDate = LocalDate.of(1900, 1, 1);
        }
        this.startDate = startDate;
        this.startDateTime = startDate.atStartOfDay();
    }

    private void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            endDate = LocalDate.of(2100, 12, 31);
        }
        this.endDate = endDate;
        this.endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
    }

    public void repeatTimeValidity() {
        if (this.startDateTime.isAfter(this.endDateTime)) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }

    //    public LocalDateTime getStartDateTime() {
//        if (this.startDateTime == null) {
//            this.startDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
//            return this.startDateTime;
//        }
//        return this.startDateTime;
//    }
//
//    public LocalDateTime getEndDateTime() {
//        if (this.endDateTime == null) {
//            this.endDateTime = LocalDateTime.of(2100, 12, 31, 23, 59);
//            return this.endDateTime;
//        }
//        return this.endDateTime;
//    }

}
