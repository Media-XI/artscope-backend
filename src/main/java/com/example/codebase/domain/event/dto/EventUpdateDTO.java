package com.example.codebase.domain.event.dto;

import com.example.codebase.domain.event.entity.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventUpdateDTO {

    @Parameter(required = false)
    private String title;

    @Parameter(required = false)
    private String description;

    @Parameter(required = false)
    private String price;

    @Parameter(required = false)
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요. ")
    private String link;

    @Parameter(required = false)
    private EventType eventType;

    @Parameter(required = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Parameter(required = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Parameter(required = false)
    private String detailedSchedule;

    @Parameter(required = false)
    private String detailLocation;

    @Parameter(required = false)
    private Long locationId;

}
