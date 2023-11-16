package com.example.codebase.domain.artwork.dto;

import com.example.codebase.controller.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArtworkLikeMembersPageDTO {

    private List<String> memberUsernames;

    private Long likes;

    private PageInfo pageInfo;

    public static ArtworkLikeMembersPageDTO from(List<String> memberUsernames, Long likes, PageInfo pageInfo) {
        ArtworkLikeMembersPageDTO artworkLikeMembersPageDTO = new ArtworkLikeMembersPageDTO();
        artworkLikeMembersPageDTO.setMemberUsernames(memberUsernames);
        artworkLikeMembersPageDTO.setLikes(likes);
        artworkLikeMembersPageDTO.setPageInfo(pageInfo);
        return artworkLikeMembersPageDTO;
    }
}
