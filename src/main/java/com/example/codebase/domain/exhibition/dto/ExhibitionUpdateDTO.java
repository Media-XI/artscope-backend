package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.EventType;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class ExhibitionUpdateDTO {

    @Parameter(required = false)
    private String title;

    @Parameter(required = false)
    private String description;

    private String price;

    @Parameter(required = false)
    @Pattern(regexp = "^(http|https)://.*", message = "웹사이트 주소를 입력해주세요. ")
    private String link;

    @Parameter(required = false)
    private EventType eventType;
}
