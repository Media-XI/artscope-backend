package com.example.codebase.domain.agora.dto;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraMedia;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Integer naturalCount;

    private Integer disagreeCount;

    private Integer participantCount;

    private String agreeText;

    private String naturalText;

    private String disagreeText;

    private Boolean isAnonymous;

    @Builder.Default
    @JsonIgnore
    private Boolean isUserVoteCancle = false; // 현재 유저가 투표했는지 여부

    private AgoraParticipantResponseDTO author;

    private AgoraMediaResponseDTO thumbnail;

    private List<AgoraMediaResponseDTO> medias;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTime;

    public static AgoraReponseDTO from(Agora agora) {
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
                .agreeCount(agora.getAgreeCount())
                .naturalCount(agora.getNaturalCount())
                .disagreeCount(agora.getDisagreeCount())
                .participantCount(agora.getParticipantCount())
                .agreeText(agora.getAgreeText())
                .naturalText(agora.getNaturalText())
                .disagreeText(agora.getDisagreeText())
                .isUserVoteCancle(false)
                .isAnonymous(agora.getIsAnonymous())
                .createdTime(agora.getCreatedTime())
                .updatedTime(agora.getUpdatedTime())
                .author(agoraParticipantResponseDTO)
                .thumbnail(thumbnail)
                .medias(medias)
                .build();
    }

    public static AgoraReponseDTO of(Agora agora, boolean userVoted) {
        AgoraReponseDTO from = from(agora);
        from.setIsUserVoteCancle(userVoted);
        return from;
    }
}