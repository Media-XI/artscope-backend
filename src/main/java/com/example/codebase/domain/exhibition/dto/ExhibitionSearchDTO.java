package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionSearchDTO {

    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.of(1900, 1, 1);

    @Builder.Default
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.of(2100, 12, 31);

    @NotNull
    private String eventType;

    @JsonIgnore
    private LocalDateTime startDateTime;

    @JsonIgnore
    private LocalDateTime endDateTime;

    public void repeatTimeValidity() {
        if (this.startDateTime.isAfter(this.endDateTime)) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }


    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.startDateTime = startDate.atStartOfDay();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
    }
}
