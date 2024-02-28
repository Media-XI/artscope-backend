package com.example.codebase.domain.follow.entity;

import com.example.codebase.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(FollowIds.class)
public class Follow implements Persistable<FollowIds>{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", columnDefinition = "BINARY(16)")
    private Member follower;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", columnDefinition = "BINARY(16)")
    private Member following;

    @Column(name = "follow_time")
    @Builder.Default
    private LocalDateTime followTime = null;

    public static Follow of(Member follower, Member following) {
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }

    @Override
    public FollowIds getId() {
        return new FollowIds(this.follower.getId(), this.following.getId());
    }

    @Override
    @Transient
    public boolean isNew() {
       if(this.followTime == null) {
           this.followTime = LocalDateTime.now();
           return true;
       }
       return false;
    }

}
