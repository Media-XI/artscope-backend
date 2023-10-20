package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PostLikeMemberDTO {

    private String username;

    private String name;

    private LocalDateTime likedTime;

    public static PostLikeMemberDTO of(Member member, LocalDateTime likedTime) {
        return PostLikeMemberDTO.builder()
                .username(member.getUsername())
                .name(member.getName())
                .likedTime(likedTime)
                .build();
    }
}
