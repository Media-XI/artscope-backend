package com.example.codebase.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
public class EventSearchDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.of(1900, 1, 1);

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.of(2100, 12, 31);

    @NotBlank(message = "이벤트 타입은 필수입니다.")
    private String eventType;

    private String username;

    public void repeatTimeValidity() {
        if (this.startDate.isAfter(this.endDate)) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }
}
