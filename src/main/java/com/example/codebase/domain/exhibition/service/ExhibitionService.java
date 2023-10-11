package com.example.codebase.domain.exhibition.service;

import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkResponseDTO;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {
    private final ExhibitionRepository exhibitionRepository;
    private final ArtworkRepository artworkRepository;

    private final MemberRepository memberRepository;

    @Autowired
    public ExhibitionService(ExhibitionRepository exhibitionRepository, ArtworkRepository artworkRepository, MemberRepository memberRepository) {
        this.exhibitionRepository = exhibitionRepository;
        this.artworkRepository = artworkRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ResponseExhibitionDTO createExhibition(CreateExhibitionDTO dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundMemberException());

        Exhibition exhibition = Exhibition.of(dto, member);

        for (ExhibitionMediaCreateDTO mediaCreateDTO : dto.getMediaUrls()) {
            ExhibitionMedia media = ExhibitionMedia.of(mediaCreateDTO, exhibition);
            exhibition.addExhibitionMedia(media);
        }

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
                .orElseThrow(() -> new NotFoundException("전시를 찾을 수 없습니다."));

        if (!exhibition.getMember().getUsername().equals(username)) {
            throw new RuntimeException("공모전의 작성자가 아닙니다.");
        }

        exhibition.update(createExhibitionDTO);

        return ResponseExhibitionDTO.from(exhibition);
    }

    @Transactional
    public void deleteExhibition(Long exhibitionId, String username) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new NotFoundException("전시를 찾을 수 없습니다."));

        if (!exhibition.getMember().getUsername().equals(username)) {
            throw new RuntimeException("공모전의 작성자가 아닙니다.");
        }

        exhibition.delete(); // 소프트 삭제
    }

}