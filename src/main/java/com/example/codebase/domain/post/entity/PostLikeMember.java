package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_like_member")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostLikeMemberIds.class)
public class PostLikeMember {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "liked_time")
    private LocalDateTime likedTime;

    public static PostLikeMember of(Post post, Member member) {
        return PostLikeMember.builder()
                .member(member)
                .post(post)
                .likedTime(LocalDateTime.now())
                .build();
    }
}
