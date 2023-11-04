package com.example.codebase.domain.agora.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @JoinColumns(
            {
                    @JoinColumn(name = "agora_id", referencedColumnName = "agora_id", insertable = false, updatable = false),
                    @JoinColumn(name = "author_id", referencedColumnName = "member_id", insertable = false, updatable = false)
            }
    )
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
