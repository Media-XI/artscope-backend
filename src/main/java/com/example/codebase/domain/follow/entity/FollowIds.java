package com.example.codebase.domain.follow.entity;

import com.example.codebase.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowIds implements Serializable {

    private UUID follower;

    private UUID following;


    public static FollowIds of(Member follower, Member following) {
        return new FollowIds(follower.getId(), following.getId());
    }

    public void valid() {
        if (follower == following) {
            throw new RuntimeException("자신을 팔로우 할 수 없습니다.");
        }
    }

}
