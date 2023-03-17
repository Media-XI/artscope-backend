package com.example.codebase.domain.member.dto;

import lombok.*;

import java.util.Set;
import java.util.stream.Collector;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {
    private String authorityName;

}
