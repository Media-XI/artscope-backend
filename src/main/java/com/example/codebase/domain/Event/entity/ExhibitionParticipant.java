package com.example.codebase.domain.Event.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "event_participant")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_participant_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
    }

}
