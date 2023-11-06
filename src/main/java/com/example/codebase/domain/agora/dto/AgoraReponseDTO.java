package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private AgoraParticipantResponseDTO author;

    private AgoraMediaResponseDTO thumbnail;

    private List<AgoraMediaResponseDTO> medias;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static AgoraReponseDTO of(Agora agora, Integer agreeCount, Integer disagreeCount,
                                     Integer participantCount) {
        List<AgoraMedia> agoraMedia = agora.getMedias();

        List<AgoraMediaResponseDTO> medias =
                agoraMedia.stream().map(AgoraMediaResponseDTO::from).collect(Collectors.toList());

        AgoraMediaResponseDTO thumbnail = medias.stream().findFirst().orElse(null);

        AgoraParticipantResponseDTO agoraParticipantResponseDTO = AgoraParticipantResponseDTO.of(agora.getAuthor(),
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
                .author(agoraParticipantResponseDTO)
                .thumbnail(thumbnail)
                .medias(medias)
                .build();
    }

}
