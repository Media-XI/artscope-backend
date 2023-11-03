package com.example.codebase.domain.post.dto;

import com.example.codebase.domain.member.entity.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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
