package com.example.codebase.domain.exhibition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionSearchDTO {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String endDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime startLocalDateTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime endLocalDateTime;

    @NotNull
    private String eventType;

    public void repeatTimeValidity() {
        if (this.getStartLocalDateTime().isAfter(this.getEndLocalDateTime())) {
            throw new RuntimeException("시작일은 종료일보다 이전에 있어야 합니다.");
        }
    }

    public void convertAndSetLocalDateTimes() {
        this.startLocalDateTime = LocalDateTime.of(LocalDate.parse(this.startDate), LocalTime.MIN);
        this.endLocalDateTime = LocalDateTime.of(LocalDate.parse(this.endDate), LocalTime.MAX);
    }

}
