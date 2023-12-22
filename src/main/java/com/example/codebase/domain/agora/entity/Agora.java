package com.example.codebase.domain.agora.entity;

import com.example.codebase.domain.agora.dto.AgoraCreateDTO;
import com.example.codebase.domain.agora.dto.AgoraUpdateDTO;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agora")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    private String naturalText;

    private Integer agreeCount;

    private Integer naturalCount;

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

    @Getter
    @Builder.Default
    @OneToMany(mappedBy = "agora", cascade = CascadeType.ALL)
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
            .naturalText(dto.getNaturalText())
            .disagreeText(dto.getDisagreeText())
            .agreeCount(0)
            .naturalCount(0)
            .disagreeCount(0)
            .participantCount(0)
            .isAnonymous(dto.getIsAnonymous())
            .createdTime(LocalDateTime.now())
            .updatedTime(LocalDateTime.now())
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

    public Boolean isAuthorUsername(String username) {
        return this.author.getUsername().equals(username);
    }

    /**
     * 아고라 삭제 (isDeleted = true) SOFT DELETE
     */
    public void delete() {
        this.isDeleted = true;
    }

    /**
     * 아고라 삭제 여부 확인
     * E2E 테스트 시 동일 트랜잭션 내에서 삭제된 아고라를 다시 조회하는 경우
     * 영속성 컨텍스트에서 1차 캐싱된 엔티티를 찾기 때문에 @Where(clause = "is_deleted = 0") 조건이 적용되지 않음
     * 따라서 이를 해결하기 위해서 삭제 여부를 확인하는 메서드를 추가함
     */
    public void isDeleted() {
        if (this.isDeleted) {
            throw new NotFoundException("존재하지 않는 아고라입니다");
        }
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

    public void update(AgoraUpdateDTO dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.agreeText = dto.getAgreeText();
        this.naturalText = dto.getNaturalText();
        this.disagreeText = dto.getDisagreeText();
        this.isAnonymous = dto.getIsAnonymous();
        this.updatedTime = LocalDateTime.now();
    }

    public boolean isCorrectVoteText(String vote) {
        return this.agreeText.equals(vote) || this.disagreeText.equals(vote) || this.naturalText.equals(vote);
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
        } else if (naturalText.equals(vote)) {
            naturalCount++;
        }
    }

    public void decreaseVoteCount(String vote) {
        if (agreeText.equals(vote)) {
            agreeCount--;
        } else if (disagreeText.equals(vote)) {
            disagreeCount--;
        } else if (naturalText.equals(vote)) {
            naturalCount--;
        }
    }

    public void removeOpinion(AgoraOpinion agoraOpinion) {
        this.opinions.remove(agoraOpinion);
    }

    public Integer getOpinionSize() {
        return this.opinions.size();
    }

    public int getParticipantsSize() {
        return this.participants.size();
    }

    public boolean isAuthor(String username) {
        return this.author.equalsUsername(username);
    }
}
