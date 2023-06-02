package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ArtworkWithIsLikeResponseDTO {
    ArtworkResponseDTO artwork;

    Boolean isLike;

    public static ArtworkWithIsLikeResponseDTO from(ArtworkWithIsLike artworkWithIsLike) {
        ArtworkWithIsLikeResponseDTO artworkWithIsLikeResponseDTO = new ArtworkWithIsLikeResponseDTO();
        artworkWithIsLikeResponseDTO.setArtwork(ArtworkResponseDTO.from(artworkWithIsLike.getArtwork()));
        artworkWithIsLikeResponseDTO.setIsLike(artworkWithIsLike.getIsLike());
        return artworkWithIsLikeResponseDTO;
    }

    public static ArtworkWithIsLikeResponseDTO from(Artwork artwork) {
        ArtworkWithIsLikeResponseDTO artworkWithIsLikeResponseDTO = new ArtworkWithIsLikeResponseDTO();
        artworkWithIsLikeResponseDTO.setArtwork(ArtworkResponseDTO.from(artwork));
        artworkWithIsLikeResponseDTO.setIsLike(false);
        return artworkWithIsLikeResponseDTO;
    }

}
