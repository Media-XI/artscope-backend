package com.example.codebase.domain.exhibition.dto;

import com.example.codebase.domain.artwork.dto.ArtworkMediaResponseDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.member.dto.MemberResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ResponseExhibitionDTO {
    private Long id;

    private String title;

    private String description;

    private ExhibitionMediaResponseDTO thumbnail;

    private String link;

    // private List<ExhibitionMediaResponseDTO> exhibitionMedias; // TODO: 추후 List 로 한다면 추가

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    private String author;

    public static ResponseExhibitionDTO from(Exhibition exhibition) {
        ExhibitionMediaResponseDTO thumbnail = exhibition.getExhibitionMedias().stream()
                .findFirst()
                .map(ExhibitionMediaResponseDTO::from)
                .orElse(null);

        ResponseExhibitionDTO dto = new ResponseExhibitionDTO();
        dto.setId(exhibition.getId());
        dto.setTitle(exhibition.getTitle());
        dto.setDescription(exhibition.getDescription());
        dto.setStartDate(exhibition.getStartDate());
        dto.setLink(exhibition.getLink());
        dto.setThumbnail(thumbnail);
        dto.setEndDate(exhibition.getEndDate());
        dto.setCreatedTime(exhibition.getCreatedTime());
        dto.setUpdatedTime(exhibition.getUpdatedTime());
        dto.setAuthor(exhibition.getMember().getUsername());
        return dto;
    }
}
