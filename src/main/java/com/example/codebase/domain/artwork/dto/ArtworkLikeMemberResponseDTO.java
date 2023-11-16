package com.example.codebase.domain.artwork.dto;


import com.example.codebase.domain.artwork.entity.ArtworkLikeMember;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArtworkLikeMemberResponseDTO {

    private Long artworkId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime likedTime;

    public static ArtworkLikeMemberResponseDTO from(ArtworkLikeMember artworkLikeMember) {
        ArtworkLikeMemberResponseDTO artworkLikeMemberResponseDTO = new ArtworkLikeMemberResponseDTO();
        artworkLikeMemberResponseDTO.setArtworkId(artworkLikeMember.getArtwork().getId());
        artworkLikeMemberResponseDTO.setLikedTime(artworkLikeMember.getLikedTime());
        return artworkLikeMemberResponseDTO;
    }
}
