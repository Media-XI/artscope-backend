package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.agora.dto.AgoraCreateDTO;
import com.example.codebase.domain.agora.dto.AgoraUpdateDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agora")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Where(clause = "is_deleted = 0")
public class Agora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_id")
    private Long id;

    private String title;

    private String content;

    private String agreeText;

    private String disagreeText;

    private Integer agreeCount;

    private Integer disagreeCount;

    private Integer participantCount;

    @Builder.Default
    private Boolean isAnonymous = false;

    @Builder.Default
    private Boolean isDeleted = false;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    @Builder.Default
    @OneToMany(mappedBy = "agora", cascade = CascadeType.ALL)
    private List<AgoraMedia> medias = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "agora", cascade = CascadeType.ALL)
    private List<AgoraParticipant> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "agora", cascade = CascadeType.ALL)
    private List<AgoraOpinion> opinions = new ArrayList<>();

    public static Agora from(AgoraCreateDTO dto) {
        return Agora.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .agreeText(dto.getAgreeText())
                .disagreeText(dto.getDisagreeText())
                .agreeCount(0)
                .disagreeCount(0)
                .participantCount(0)
                .isAnonymous(dto.getIsAnonymous())
                .createdTime(LocalDateTime.now())
                .build();
    }

    public static Agora of(AgoraCreateDTO dto, Member member) {
        Agora agora = from(dto);
        agora.setMember(member);
        return agora;
    }

    private void setMember(Member member) {
        this.author = member;
        member.addAgora(this);
    }

    /**
     * 아고라 삭제 (isDeleted = true) SOFT DELETE
     */
    public void delete() {
        this.isDeleted = true;
        this.opinions.forEach(AgoraOpinion::delete);
        this.participants.forEach(AgoraParticipant::delete);
    }

    public void addMedia(AgoraMedia agoraMedia) {
        this.medias.add(agoraMedia);
    }

    public void addParticipant(AgoraParticipant agoraParticipant) {
        this.participants.add(agoraParticipant);
        this.participantCount = this.participants.size();
    }

    public void addOpinion(AgoraOpinion agoraOpinion) {
        this.opinions.add(agoraOpinion);
    }

    public void setVoteCount(Integer agreeCount, Integer disagreeCount) {
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
    }

    public void update(AgoraUpdateDTO dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.agreeText = dto.getAgreeText();
        this.disagreeText = dto.getDisagreeText();
        this.isAnonymous = dto.getIsAnonymous();
    }

    public boolean isCorrectVoteText(String vote){
        return this.getAgreeText().equals(vote) || this.getDisagreeText().equals(vote);
    }

    public void removeMedia(AgoraMedia agoraMedia) {
        this.medias.remove(agoraMedia);
    }

    // TODO : 동기화 문제가 있을 수 있음
    public void increaseVoteCount(String vote) {
        if (agreeText.equals(vote)) {
            agreeCount++;
        } else if (disagreeText.equals(vote)) {
            disagreeCount++;
        }
    }

    public void decreaseVoteCount(String vote) {
        if (agreeText.equals(vote)) {
            agreeCount--;
        } else if (disagreeText.equals(vote)) {
            disagreeCount--;
        }
    }
}
