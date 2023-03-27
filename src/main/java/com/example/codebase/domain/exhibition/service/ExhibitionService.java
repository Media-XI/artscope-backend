package com.example.codebase.domain.exhibition.service;

import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {
    private final ExhibitionRepository exhibitionRepository;

    private final MemberRepository memberRepository;

    public ExhibitionService(ExhibitionRepository exhibitionRepository, MemberRepository memberRepository) {
        this.exhibitionRepository = exhibitionRepository;
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

}
