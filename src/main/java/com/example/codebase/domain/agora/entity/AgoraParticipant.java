package com.example.codebase.domain.agora.entity;

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
@Table(name = "agora_participant")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = 0")
@IdClass(AgoraParticipantIds.class)
public class AgoraParticipant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id")
    private Agora agora;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String vote;

    private Integer agoraSequence;

    @Builder.Default
    private Boolean isDeleted = false;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "author")
    private List<AgoraOpinion> opinions = new ArrayList<>();

    public static AgoraParticipant of(Member member, Agora agora) {
        AgoraParticipant participant = AgoraParticipant.builder()
                .agoraSequence(0)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        participant.setAgoraAndMember(agora, member);
        return participant;
    }

    public void setAgoraAndMember(Agora agora, Member member) {
        this.agora = agora;
        this.member = member;
        agora.addParticipant(this);
    }

    public void addOpinion(AgoraOpinion opinion) {
        this.opinions.add(opinion);
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void vote(String vote, Integer agoraSequence) {
        this.vote = vote;
        this.agoraSequence = agoraSequence;
    }

    public void cancle(String vote) {
        this.vote.equals(vote);

    }
}
