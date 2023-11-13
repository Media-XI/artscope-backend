package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeMemberIds implements Serializable {

    private UUID member;

    private Long post;

    public static PostLikeMemberIds of(Member member, Post post) {
        return new PostLikeMemberIds(member.getId(), post.getId());
    }

    public static PostLikeMemberIds of(Member member, Long postId) {
        return new PostLikeMemberIds(member.getId(), postId);
    }

}
