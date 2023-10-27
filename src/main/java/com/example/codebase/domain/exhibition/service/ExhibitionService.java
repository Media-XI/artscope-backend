package com.example.codebase.domain.exhibition.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.exhibition.dto.CreateExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionDTO;
import com.example.codebase.domain.exhibition.dto.ResponseExhibitionPageInfoDTO;
import com.example.codebase.domain.exhibition.dto.SearchExhibitionDTO;
import com.example.codebase.domain.exhibition.entity.Exhibition;
import com.example.codebase.domain.exhibition.entity.ExhibitionMedia;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExhibitionService {
  private final ExhibitionRepository exhibitionRepository;
  private final ArtworkRepository artworkRepository;

  private final MemberRepository memberRepository;

  @Autowired
  public ExhibitionService(
      ExhibitionRepository exhibitionRepository,
      ArtworkRepository artworkRepository,
      MemberRepository memberRepository) {
    this.exhibitionRepository = exhibitionRepository;
    this.artworkRepository = artworkRepository;
    this.memberRepository = memberRepository;
  }

  @Transactional
  public ResponseExhibitionDTO createExhibition(CreateExhibitionDTO dto, String username) {
    Member member =
        memberRepository.findByUsername(username).orElseThrow(() -> new NotFoundMemberException());

    Exhibition exhibition = Exhibition.of(dto, member);

    for (ExhibitionMediaCreateDTO mediaCreateDTO : dto.getMediaUrls()) {
      ExhibitionMedia media = ExhibitionMedia.of(mediaCreateDTO, exhibition);
      exhibition.addExhibitionMedia(media);
    }

    Exhibition save = exhibitionRepository.save(exhibition);
    return ResponseExhibitionDTO.from(save);
  }

  public ResponseExhibitionPageInfoDTO getAllExhibition(
      SearchExhibitionDTO searchExhibitionDTO, int page, int size, String sortDirection) {

    if (searchExhibitionDTO.getStartDate().isAfter(searchExhibitionDTO.getEndDate())) {
      throw new RuntimeException("시작일이 종료일보다 늦을 수 없습니다.");
    }

    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
    PageRequest pageRequest = PageRequest.of(page, size, sort);

    Page<Exhibition> exhibitions =
        exhibitionRepository.findByStartAndEndDate(
            searchExhibitionDTO.getStartDate(), searchExhibitionDTO.getEndDate(), pageRequest);

    PageInfo pageInfo =
        PageInfo.of(page, size, exhibitions.getTotalPages(), exhibitions.getTotalElements());

    List<ResponseExhibitionDTO> dtos =
        exhibitions.getContent().stream()
            .map(ResponseExhibitionDTO::from)
            .collect(Collectors.toList());

    return ResponseExhibitionPageInfoDTO.of(dtos, pageInfo);
  }

  public ResponseExhibitionDTO updateExhibition(
      Long exhibitionId, CreateExhibitionDTO createExhibitionDTO, String username) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("전시를 찾을 수 없습니다."));

    if (!exhibition.getMember().getUsername().equals(username)) {
      throw new RuntimeException("공모전의 작성자가 아닙니다.");
    }

    exhibition.update(createExhibitionDTO);

    return ResponseExhibitionDTO.from(exhibition);
  }

  @Transactional
  public void deleteExhibition(Long exhibitionId, String username) {
    Exhibition exhibition =
        exhibitionRepository
            .findById(exhibitionId)
            .orElseThrow(() -> new NotFoundException("전시를 찾을 수 없습니다."));

    if (!exhibition.getMember().getUsername().equals(username)) {
      throw new RuntimeException("공모전의 작성자가 아닙니다.");
    }

    exhibition.delete(); // 소프트 삭제
  }
}
