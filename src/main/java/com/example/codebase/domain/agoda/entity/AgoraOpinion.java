package com.example.codebase.domain.agoda.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "agora_opinion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Where(clause = "is_deleted = 0")
public class AgoraOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_opinion_id")
    private Long id;

    private String content;

    private Boolean isDeleted;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id")
    private Agora agora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private AgoraParticipant author;

    public void setAgora(Agora agora) {
        this.agora = agora;
        agora.addOpinion(this);
    }

    public void setAuthor(AgoraParticipant author) {
        this.author = author;
        author.addOpinion(this);
    }

    public void delete() {
        this.isDeleted = true;
    }
}
