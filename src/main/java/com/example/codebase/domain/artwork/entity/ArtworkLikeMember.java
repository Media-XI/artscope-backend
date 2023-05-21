package com.example.codebase.domain.artwork.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "artwork_like_member")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@IdClass(ArtworkLikeMemberId.class)
public class ArtworkLikeMember implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Column(name = "liked_time")
    private LocalDateTime likedTime;

    public static ArtworkLikeMember of(Artwork artwork, Member member) {
        return ArtworkLikeMember.builder()
                .artwork(artwork)
                .member(member)
                .likedTime(LocalDateTime.now())
                .build();
    }


}
