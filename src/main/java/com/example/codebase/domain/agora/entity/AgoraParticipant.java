package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agora_participant")
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

    @Builder.Default
    @OneToMany(mappedBy = "author")
    private List<AgoraOpinion> opinions = new ArrayList<>();

    public static AgoraParticipant create() {
        AgoraParticipant participant = AgoraParticipant.builder()
                .agoraSequence(0)
                .createdTime(LocalDateTime.now())
                .build();
        return participant;
    }

    /**
     * 연관관계 메소드
     *
     * @param agora
     * @param member
     */
    public void setAgoraAndMember(Agora agora, Member member) {
        this.agora = agora;
        this.member = member;
        agora.addParticipant(this);
    }

    /**
     * 새로운 참가 순번 부여
     */
    public void newSequence() {
        int newSequence = this.agora.getParticipantsSize() - 1;
        this.agoraSequence = newSequence;
    }

    public void addOpinion(AgoraOpinion opinion) {
        this.opinions.add(opinion);
    }

    public void delete() {
        this.isDeleted = true;
        updateTime();
    }

    public void createVote(String vote) {
        this.vote = vote;
        increaseVoteCount(vote);
    }

    public void updateVote(String vote) {
        String oldVote = this.vote;
        cancleVote(oldVote);
        createVote(vote);
        updateTime();
    }

    public void cancleVote(String vote) {
        decreaseVoteCount(vote);
        this.vote = "";
        updateTime();
    }

    private void updateTime() {
        this.updatedTime = LocalDateTime.now();
    }

    private void increaseVoteCount(String vote) {
        agora.increaseVoteCount(vote);
    }

    private void decreaseVoteCount(String vote) {
        agora.decreaseVoteCount(vote);
    }

    public boolean hasOpinions() {
        return this.opinions.size() > 0;
    }

    /**
     * 새로운 투표자인지 확인
     */
    public boolean isNew() {
        return this.member == null && this.agora == null;
    }

    /**
     * 투표 내용이 같은지 확인
     */
    public boolean isSameVote(String vote) {
        return isVoted() && this.vote.equals(vote);
    }

    public String getVoteText() {
        return this.vote;
    }

    public String getMemberUsername() {
        return this.member.getUsername();
    }

    public Integer getSequence() {
        return this.agoraSequence;
    }

    public Agora getAgora() {
        return this.agora;
    }

    public boolean isVoted() {
        return this.vote != null && this.vote != "";
    }

    public void removeOpinion(AgoraOpinion agoraOpinion) {
        this.opinions.remove(agoraOpinion);
    }

    public String getVote() {
        return this.vote;
    }
}
