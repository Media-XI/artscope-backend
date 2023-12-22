package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

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
