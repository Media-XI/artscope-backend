package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtworkLikeResponseDTO {

    private ArtworkResponseDTO artwork;
    private boolean isLiked;
    private String status;

    public static ArtworkLikeResponseDTO from(Artwork artwork, boolean isLiked, String status) {
        ArtworkLikeResponseDTO artworkLikeResponseDTO = new ArtworkLikeResponseDTO();
        artworkLikeResponseDTO.setArtwork(ArtworkResponseDTO.from(artwork));
        artworkLikeResponseDTO.setLiked(isLiked);
        artworkLikeResponseDTO.setStatus(status);
        return artworkLikeResponseDTO;
    }
}
