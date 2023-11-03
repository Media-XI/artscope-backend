package com.example.codebase.domain.agoda.entity;

import com.example.codebase.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

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

    public void setAgoraAndMember(Agora agora, Member member) {
        this.agora = agora;
        this.member = member;
        agora.addParticipant(this);
    }

    public void addOpinion(AgoraOpinion opinion) {
        this.opinions.add(opinion);
    }

}
