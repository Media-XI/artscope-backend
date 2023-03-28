package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResponseExhibitionDTO {
    private Long id;
    private String title;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    private String memberUsername;

    public static ResponseExhibitionDTO from(Exhibition exhibition) {
        ResponseExhibitionDTO dto = new ResponseExhibitionDTO();
        dto.setId(exhibition.getId());
        dto.setTitle(exhibition.getTitle());
        dto.setDescription(exhibition.getDescription());
        dto.setStartDate(exhibition.getStartDate());
        dto.setEndDate(exhibition.getEndDate());
        dto.setCreatedTime(exhibition.getCreatedTime());
        dto.setUpdatedTime(exhibition.getUpdatedTime());
        dto.setMemberUsername(exhibition.getMember().getUsername());
        return dto;
    }
}
