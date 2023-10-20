package com.example.codebase.domain.artwork.dto;

import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkWithIsLike;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ArtworkWithIsLikeResponseDTO {

    ArtworkResponseDTO artwork;

    Boolean isLiked;

    public static ArtworkWithIsLikeResponseDTO from(ArtworkWithIsLike artworkWithIsLike) {
        ArtworkWithIsLikeResponseDTO artworkWithIsLikeResponseDTO = new ArtworkWithIsLikeResponseDTO();
        artworkWithIsLikeResponseDTO.setArtwork(ArtworkResponseDTO.from(artworkWithIsLike.getArtwork()));
        artworkWithIsLikeResponseDTO.setIsLiked(artworkWithIsLike.getIsLike());
        return artworkWithIsLikeResponseDTO;
    }

    public static ArtworkWithIsLikeResponseDTO from(Artwork artwork) {
        ArtworkWithIsLikeResponseDTO artworkWithIsLikeResponseDTO = new ArtworkWithIsLikeResponseDTO();
        artworkWithIsLikeResponseDTO.setArtwork(ArtworkResponseDTO.from(artwork));
        artworkWithIsLikeResponseDTO.setIsLiked(false);
        return artworkWithIsLikeResponseDTO;
    }

    public static ArtworkWithIsLikeResponseDTO from(Artwork artwork, boolean existLike) {
        ArtworkWithIsLikeResponseDTO artworkWithIsLikeResponseDTO = new ArtworkWithIsLikeResponseDTO();
        artworkWithIsLikeResponseDTO.setArtwork(ArtworkResponseDTO.from(artwork));
        artworkWithIsLikeResponseDTO.setIsLiked(existLike);
        return artworkWithIsLikeResponseDTO;
    }

    public static ArtworkWithIsLikeResponseDTO from(Artwork artwork, List<ArtworkCommentResponseDTO> comments, boolean existLike) {
        ArtworkWithIsLikeResponseDTO artworkWithIsLikeResponseDTO = new ArtworkWithIsLikeResponseDTO();
        artworkWithIsLikeResponseDTO.setArtwork(ArtworkResponseDTO.of(artwork, comments));
        artworkWithIsLikeResponseDTO.setIsLiked(existLike);
        return artworkWithIsLikeResponseDTO;
    }
}
