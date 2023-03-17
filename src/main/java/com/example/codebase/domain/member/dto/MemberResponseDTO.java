package com.example.codebase.domain.member.dto;

import com.example.codebase.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class MemberResponseDTO {
    private String username;
    private String name;
    private String email;
    private boolean activated;
    private Set<AuthorityDto> authrities;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    public static MemberResponseDTO from(Member member) {
        MemberResponseDTO dto = new MemberResponseDTO();
        dto.setUsername(member.getUsername());
        dto.setName(member.getName());
        dto.setEmail(member.getEmail());
        dto.setActivated(member.isActivated());
        dto.setAuthrities(
                member.getAuthorities().stream()
                        .map(authority ->
                                AuthorityDto.builder()
                                        .authorityName(authority.getAuthority().getAuthorityName())
                                        .build()
                        ).collect(Collectors.toSet())
        );
        dto.setCreatedTime(member.getCreatedTime());
        return dto;
    }

}
