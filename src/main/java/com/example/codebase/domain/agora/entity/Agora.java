package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.agora.dto.AgoraCreateDTO;
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
@Where(clause = "is_deleted = false")
public class Agora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_id")
    private Long id;

    private String title;

    private String content;

    private String agreeText;

    private String disagreeText;

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
    @OneToMany(mappedBy = "agora")
    private List<AgoraMedia> medias = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "agora")
    private List<AgoraParticipant> participants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "agora")
    private List<AgoraOpinion> opinions = new ArrayList<>();

    public static Agora from(AgoraCreateDTO dto) {
        return Agora.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .agreeText(dto.getAgreeText())
                .disagreeText(dto.getDisagreeText())
                .isAnonymous(dto.getIsAnonymous())
                .createdTime(LocalDateTime.now())
                .build();
    }

    public static Agora of(AgoraCreateDTO dto, Member member) {
        Agora agora = from(dto);
        agora.author = member;
        member.addAgora(agora);
        return agora;
    }

    public void delete() {
        this.isDeleted = true;
    }


    public void addMedia(AgoraMedia agoraMedia) {
        this.medias.add(agoraMedia);
    }

    public void addParticipant(AgoraParticipant agoraParticipant) {
        this.participants.add(agoraParticipant);
    }

    public void addOpinion(AgoraOpinion agoraOpinion) {
        this.opinions.add(agoraOpinion);
    }
}
