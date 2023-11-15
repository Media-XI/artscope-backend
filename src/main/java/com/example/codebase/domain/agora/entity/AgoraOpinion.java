package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.agora.dto.AgoraOpinionRequestDTO;
import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agora_opinion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
public class AgoraOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agora_opinion_id")
    @Getter
    private Long id;

    @Getter
    private String content;

    private Boolean isDeleted;

    @Getter
    private LocalDateTime createdTime;

    @Getter
    private LocalDateTime updatedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agora_id", insertable = false, updatable = false)
    private Agora agora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        {
            @JoinColumn(name = "agora_id", referencedColumnName = "agora_id"),
            @JoinColumn(name = "author_id", referencedColumnName = "member_id")
        }
    )
    private AgoraParticipant author;

    public static AgoraOpinion from(AgoraOpinionRequestDTO content) {
        return AgoraOpinion.builder()
            .content(content.getContent())
            .createdTime(LocalDateTime.now())
            .isDeleted(false)
            .build();
    }

    public void setAgoraAndAuthor(Agora agora, AgoraParticipant author) {
        this.setAgora(agora);
        this.setAuthor(author);
    }

    private void setAuthor(AgoraParticipant author) {
        this.author = author;
        author.addOpinion(this);
    }

    /**
     * <p>SOFT DELETE</p>
     */
    public void delete() {
        this.isDeleted = true;
        // TODO: SOFT DELETE이고, FK NOT NULL 제약조건으로 인해 OneToMany List에서 요소 삭제만 할지 의논하기
    }

    public String getAuthorVote() {
        return this.author.getVoteText();
    }

    public Integer getAuthorSequence() {
        return this.author.getSequence();
    }

    public boolean isSameVoteAndNotDeleted(String vote) {
        return this.author.isSameVote(vote) && !this.isDeleted;
    }

    public Agora getAgora() {
        return this.agora;
    }


    private void setAgora(Agora agora) {
        this.agora = agora;
        agora.addOpinion(this);
    }

    public Member getMember() {
        return author.getMember();
    }

    /**
     * @param agoraId 아고라의 id
     *                <p>주어진 agoraId가 현재 의견의 agoraId 같은지 확인</p>
     */
    public void checkAgoraId(Long agoraId) {
        if (!this.agora.getId().equals(agoraId)) {
            throw new RuntimeException("해당 아고라에 속하지 않은 의견입니다.");
        }
    }

    public boolean isAuthor(String username) {
        return this.author.getMemberUsername().equals(username);
    }

    /**
     * @param username 작성자의 username
     *                 <p>주어진 Username 이용해 의견 작성자 본인인지 확인</p>
     */
    public void checkAuthor(String username) {
        if (!isAuthor(username)) {
            throw new RuntimeException("해당 의견의 작성자가 아닙니다.");
        }
    }

    public void checkAuthorOrIsAdmin(String username, boolean isAdmin) {
        if (!isAuthor(username) && !isAdmin) {
            throw new RuntimeException("해당 의견의 작성자가 아닙니다.");
        }
    }


    public void update(AgoraOpinionRequestDTO content) {
        this.content = content.getContent();
        this.updatedTime = LocalDateTime.now();
    }

}
