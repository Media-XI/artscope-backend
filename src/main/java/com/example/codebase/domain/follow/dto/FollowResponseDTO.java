package com.example.codebase.domain.follow.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowResponseDTO {

    private List<FollowDetailResponseDTO> follows;

    private PageInfo pageInfo;

    public static FollowResponseDTO of(List<FollowDetailResponseDTO> followDetailResponseDTO, PageInfo pageInfo ) {
        return FollowResponseDTO.builder()
                .follows(followDetailResponseDTO)
                .pageInfo(pageInfo)
                .build();
    }
}
