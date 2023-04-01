package com.example.codebase.domain.exhibition.service;

import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.exhibition_artwork.dto.ExhibitionArtworkResponseDTO;
import com.example.codebase.domain.exhibition_artwork.dto.ExhibitionArtworksResponseDTO;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtwork;
import com.example.codebase.domain.exhibition_artwork.entity.ExhibitionArtworkStatus;
import com.example.codebase.domain.exhibition_artwork.exception.NotFoundExhibitionException;
import com.example.codebase.domain.exhibition_artwork.repository.ExhibitionArtworkRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {
    private final ExhibitionRepository exhibitionRepository;

    private final ExhibitionArtworkRepository exhibitionArtworkRepository;

    private final ArtworkRepository artworkRepository;

    private final MemberRepository memberRepository;

    public ExhibitionService(ExhibitionRepository exhibitionRepository, ExhibitionArtworkRepository exhibitionArtworkRepository, ArtworkRepository artworkRepository, MemberRepository memberRepository) {
        this.exhibitionRepository = exhibitionRepository;
        this.exhibitionArtworkRepository = exhibitionArtworkRepository;
        this.artworkRepository = artworkRepository;
        this.memberRepository = memberRepository;
    }

    public ResponseExhibitionDTO createExhibition(CreateExhibitionDTO dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundMemberException());

        Exhibition exhibition = Exhibition.of(dto, member);
        Exhibition save = exhibitionRepository.save(exhibition);

        return ResponseExhibitionDTO.from(save);
    }

    public List<ResponseExhibitionDTO> getAllExhibition() {
        List<Exhibition> exhibitions = exhibitionRepository.findAll();

        List<ResponseExhibitionDTO> dtos = exhibitions.stream()
                .map(ResponseExhibitionDTO::from)
                .collect(Collectors.toList());

        return dtos;
    }

    public ResponseExhibitionDTO updateExhibition(Long exhibitionId, CreateExhibitionDTO createExhibitionDTO, String username) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new NotFoundExhibitionException());

        if (!exhibition.getMember().getUsername().equals(username)) {
            throw new RuntimeException("공모전의 작성자가 아닙니다.");
        }

        exhibition.update(createExhibitionDTO);

        return ResponseExhibitionDTO.from(exhibition);
    }

    public void deleteExhibition(Long exhibitionId, String username) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new NotFoundExhibitionException());

        if (!exhibition.getMember().getUsername().equals(username)) {
            throw new RuntimeException("공모전의 작성자가 아닙니다.");
        }

        // 공모전에 제출된 작품들을 반려처리
        List<ExhibitionArtwork> all = exhibitionArtworkRepository.findAllByExhibitionId(exhibitionId);
        all.forEach(exhibitionArtwork -> {
            exhibitionArtwork.setExhibitionArtworkStatus(ExhibitionArtworkStatus.rejected);
        });

        exhibition.delete(); // 소프트 삭제
    }

    public ExhibitionArtworkResponseDTO addArtworkToExhibition(Long exhibitionId, Long artworkId, String username) {
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 작품입니다."));

        if (!artwork.getMember().getUsername().equals(username)) {
            throw new RuntimeException("아트워크의 작성자가 아닙니다.");
        }

        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new NotFoundExhibitionException());

        ExhibitionArtwork exhibitionArtwork = ExhibitionArtwork.of(exhibition, artwork);
        artwork.addExhibitionArtwork(exhibitionArtwork);

        ExhibitionArtwork save = exhibitionArtworkRepository.save(exhibitionArtwork);

        return ExhibitionArtworkResponseDTO.from(save);
    }

    public ExhibitionArtworksResponseDTO getArtworkFromExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new NotFoundExhibitionException());

        List<ExhibitionArtwork> all = exhibitionArtworkRepository.findAllByExhibitionId(exhibitionId);
        List<ArtworkResponseDTO> artworkResponseDTOS = all.stream()
                .map(ExhibitionArtwork::getArtwork)
                .map(ArtworkResponseDTO::from)
                .collect(Collectors.toList());

        return ExhibitionArtworksResponseDTO.from(exhibition, artworkResponseDTOS);
    }

    public ExhibitionArtworkResponseDTO updateStatusExhibitionArtwork(Long exhibitionId, Long artworkId, String status) {
        ExhibitionArtwork exhibitionArtwork = exhibitionArtworkRepository.findByExhibitionIdAndArtworkId(exhibitionId, artworkId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공모전 또는 작품입니다."));

        exhibitionArtwork.updateStatus(status);

        return ExhibitionArtworkResponseDTO.from(exhibitionArtwork);
    }

    public void deleteExhibitionArtwork(Long exhibitionId, Long artworkId, String username) {
        ExhibitionArtwork exhibitionArtwork = exhibitionArtworkRepository.findByExhibitionIdAndArtworkId(exhibitionId, artworkId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 공모전 또는 작품입니다."));

        if (!exhibitionArtwork.getArtwork().getMember().getUsername().equals(username)) {
            throw new RuntimeException("아트워크의 작성자가 아닙니다.");
        }

        exhibitionArtworkRepository.delete(exhibitionArtwork);
    }
}