package com.example.codebase.domain.post.entity;

import com.example.codebase.domain.member.entity.Member;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
