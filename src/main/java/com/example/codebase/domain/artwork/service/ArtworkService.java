package com.example.codebase.domain.artwork.service;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.repository.ArtworkMediaRepository;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtworkService {
    private final ArtworkRepository artworkRepository;
    private final ArtworkMediaRepository artworkMediaRepository;

    private final MemberRepository memberRepository;


    public ArtworkService(ArtworkRepository artworkRepository, ArtworkMediaRepository artworkMediaRepository, MemberRepository memberRepository) {
        this.artworkRepository = artworkRepository;
        this.artworkMediaRepository = artworkMediaRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ArtworkResponseDTO createArtwork(ArtworkCreateDTO dto, String username) {
        if (dto.getMediaUrls().size() >= 5) {
            throw new RuntimeException("미디어 파일 개수는 5개까지만 등록할 수 있습니다.");
        }

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Artwork artwork = Artwork.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdTime(LocalDateTime.now())
                .member(member)
                .build();

        for (ArtworkMediaCreateDTO mediaCreateDTO : dto.getMediaUrls()) {
            ArtworkMedia media = ArtworkMedia.of(mediaCreateDTO, artwork);
            artwork.addArtworkMedia(media);
        }

        Artwork saveArtwork = artworkRepository.save(artwork);
        return ArtworkResponseDTO.from(saveArtwork);
    }

    public List<ArtworkResponseDTO> getAllArtwork() {
        List<ArtworkResponseDTO> dtos = artworkRepository.findAll().stream()
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());
        return dtos;
    }
}
