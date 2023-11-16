package com.example.codebase.domain.agora.dto;

import lombok.Builder;
import lombok.Getter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.beans.ConstructorProperties;
import java.util.List;

@Getter
public class AgoraCreateDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "동의에 대한 문구는 필수입니다.")
    private String agreeText;

    @NotBlank(message = "중립에 대한 문구는 필수입니다.")
    private String naturalText;

    @NotBlank(message = "반대에 대한 문구는 필수입니다.")
    private String disagreeText;

    @NotNull(message = "익명 여부는 필수입니다.")
    private Boolean isAnonymous;

    @Valid
    private List<AgoraMediaCreateDTO> medias;

    @Valid
    private AgoraMediaCreateDTO thumbnail;

    @Builder
    @ConstructorProperties({"title", "content", "agreeText", "naturalText", "disagreeText", "isAnonymous", "medias", "thumbnail"})
    public AgoraCreateDTO(String title, String content, String agreeText, String naturalText, String disagreeText, Boolean isAnonymous, List<AgoraMediaCreateDTO> medias, AgoraMediaCreateDTO thumbnail) {
        this.title = title;
        this.content = content;
        this.agreeText = agreeText;
        this.naturalText = naturalText;
        this.disagreeText = disagreeText;
        this.isAnonymous = isAnonymous;
        this.medias = medias;
        this.thumbnail = thumbnail;
        checkVoteMessage();
    }

    private void checkVoteMessage() {
        boolean isSameVoteMessages = agreeText.equals(naturalText) || naturalText.equals(disagreeText) || agreeText.equals(disagreeText);
        if (isSameVoteMessages) {
            throw new IllegalArgumentException("동의, 중립, 반대에 대한 문구는 모두 다른 문구여야 합니다.");
        }
    }

    public boolean isThumbnailNull() {
        return this.thumbnail == null;
    }

    public boolean isMediasNull() {
        return this.medias == null;
    }



}
