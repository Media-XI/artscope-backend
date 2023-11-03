package com.example.codebase.domain.agoda.dto;

import com.example.codebase.domain.agoda.entity.Agora;
import com.example.codebase.domain.agoda.entity.AgoraMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgoraReponseDTO {

    private Long id;

    private String title;

    private String content;

    private Integer agreeCount;

    private Integer disagreeCount;

    private Integer participantCount;

    private String agreeText;

    private String disagreeText;

    private Boolean isAnonymous;

    private AgoraParicipantResponseDTO author;

    private AgoraMediaResponseDTO thumbnail;

    private List<AgoraMediaResponseDTO> medias;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static AgoraReponseDTO from(Agora agora, Integer agreeCount, Integer disagreeCount,
                                       Integer participantCount) {
        List<AgoraMedia> agoraMedia = agora.getMedias();

        List<AgoraMediaResponseDTO> medias =
                agoraMedia.stream().map(AgoraMediaResponseDTO::from).collect(Collectors.toList());

        AgoraMediaResponseDTO thumbnail = medias.stream().findFirst().orElse(null);

        AgoraParicipantResponseDTO agoraParicipantResponseDTO = AgoraParicipantResponseDTO.from(agora.getAuthor(),
                agora.getIsAnonymous(), 0);

        return AgoraReponseDTO.builder()
                .id(agora.getId())
                .title(agora.getTitle())
                .content(agora.getContent())
                .agreeCount(agreeCount)
                .disagreeCount(disagreeCount)
                .participantCount(participantCount)
                .agreeText(agora.getAgreeText())
                .disagreeText(agora.getDisagreeText())
                .isAnonymous(agora.getIsAnonymous())
                .createdTime(agora.getCreatedTime())
                .updatedTime(agora.getUpdatedTime())
                .author(agoraParicipantResponseDTO)
                .thumbnail(thumbnail)
                .medias(medias)
                .build();
    }

}
