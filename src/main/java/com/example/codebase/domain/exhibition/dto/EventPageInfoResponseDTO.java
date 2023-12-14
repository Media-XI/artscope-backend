package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventPageInfoResponseDTO {

    List<EventResponseDTO> events;

    PageInfo pageInfo;

    public static EventPageInfoResponseDTO of(
        List<EventResponseDTO> dtos, PageInfo pageInfo) {
        EventPageInfoResponseDTO responseDTO = new EventPageInfoResponseDTO();
        responseDTO.events = dtos;
        responseDTO.pageInfo = pageInfo;
        return responseDTO;
    }
}
