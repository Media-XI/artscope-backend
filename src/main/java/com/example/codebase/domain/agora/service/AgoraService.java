package com.example.codebase.domain.agora.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.agora.dto.*;
import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraMedia;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import com.example.codebase.domain.agora.entity.AgoraParticipantIds;
import com.example.codebase.domain.agora.repository.AgoraMediaRepository;
import com.example.codebase.domain.agora.repository.AgoraParticipantRepository;
import com.example.codebase.domain.agora.repository.AgoraRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.NotFoundException;
import com.example.codebase.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgoraService {

    private final MemberRepository memberRepository;
    private final AgoraRepository agoraRepository;
    private final AgoraParticipantRepository agoraParticipantRepository;
    private final AgoraMediaRepository agoraMediaRepository;
    private final S3Service s3Service;

    @Autowired
    public AgoraService(MemberRepository memberRepository, AgoraRepository agoraRepository, AgoraParticipantRepository agoraParticipantRepository, AgoraMediaRepository agoraMediaRepository, S3Service s3Service) {
        this.memberRepository = memberRepository;
        this.agoraRepository = agoraRepository;
        this.agoraParticipantRepository = agoraParticipantRepository;
        this.agoraMediaRepository = agoraMediaRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public AgoraReponseDTO createAgora(AgoraCreateDTO dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = Agora.of(dto, member);

        // 썸네일 추가
        AgoraMedia thumbnail = AgoraMedia.from(dto.getThumbnail());
        thumbnail.setAgora(agora);

        // 미디어 추가
        dto.getMedias().forEach(mediaDTO -> {
            AgoraMedia agoraMedia = AgoraMedia.from(mediaDTO);
            agoraMedia.setAgora(agora);
        });

        // 작성자를 아고라 참여자로 추가
        AgoraParticipant participant = AgoraParticipant.create();
        participant.setAgoraAndMember(agora, member);

        agoraRepository.save(agora);
        return AgoraReponseDTO.from(agora);
    }

    @Transactional(readOnly = true)
    public AgorasResponseDTO getAllAgora(PageRequest pageRequest) {
        Page<Agora> agoras = agoraRepository.findAll(pageRequest);
        PageInfo pageInfo = PageInfo.from(agoras);

        List<AgoraReponseDTO> agoraReponseDTOS = agoras.getContent().stream()
                .map(AgoraReponseDTO::from)
                .collect(Collectors.toList());

        return AgorasResponseDTO.of(agoraReponseDTOS, pageInfo);
    }

    @Transactional(readOnly = true)
    public AgoraDetailReponseDTO getAgora(Long agoraId) {
        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않는 아고라입니다.");
                });

        // 아고라에 달린 의견 조회
        // TODO : 삭제된 의견 및 참여자 제외할 로직 추가
        List<AgoraOpinionResponseDTO> agreeOpinionDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.getAuthor().isSameVote(agora.getAgreeText()))
                .map(AgoraOpinionResponseDTO::from)
                .collect(Collectors.toList());

        List<AgoraOpinionResponseDTO> disagreeOpinionDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.getAuthor().isSameVote(agora.getDisagreeText()))
                .map(AgoraOpinionResponseDTO::from)
                .collect(Collectors.toList());

        AgoraReponseDTO agoraDTO = AgoraReponseDTO.from(agora);
        return AgoraDetailReponseDTO.of(agoraDTO, agreeOpinionDTOs, disagreeOpinionDTOs);
    }

    @Transactional
    public AgoraReponseDTO updateAgora(Long agoraId, AgoraUpdateDTO dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않는 아고라입니다.");
                });

        if (agora.getAuthor() != member) {
            throw new RuntimeException("아고라의 작성자가 아닙니다.");
        }

        if (agora.getOpinions().size() > 0) {
            throw new RuntimeException("아고라에 달린 의견이 있습니다.");
        }

        agora.update(dto);

        return AgoraReponseDTO.from(agora);
    }

    @Transactional
    public void deleteAgora(Long agoraId, String username) {
        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않는 아고라입니다.");
                });

        if (agora.getAuthor().equals(username)) {
            throw new RuntimeException("아고라의 작성자가 아닙니다.");
        }

        if (agora.getOpinions().size() > 0) {
            throw new RuntimeException("아고라에 달린 의견이 있습니다.");
        }

        // 정적 이미지 파일(S3) 삭제
        for (AgoraMedia media : agora.getMedias()) {
            media.delete();
            s3Service.deleteObject(media.getMediaUrl());
            agoraMediaRepository.delete(media);
        }

        agora.delete();
    }


    @Transactional
    public AgoraReponseDTO voteAgora(Long agoraId, String vote, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않는 아고라입니다.");
                });

        if (!agora.isCorrectVoteText(vote)) {
            throw new RuntimeException("투표 내용이 올바르지 않습니다.");
        }

        AgoraParticipant participant = agoraParticipantRepository.findById(AgoraParticipantIds.of(agora, member))
                .orElse(AgoraParticipant.create()); // 아니면 새로운 투표자로 등록

        if (participant.hasOpinions()) {
            throw new RuntimeException("이미 의견을 작성한 사람은 투표를 변경할 수 없습니다.");
        }

        boolean isVoteCancle = false;
        if (participant.isNewParticipant()) {
            participant.setAgoraAndMember(agora, member);
            participant.newSequence();
            participant.newVote(vote);
        } else {
            if (participant.isSameVote(vote)) {
                participant.cancleVote(vote);
                isVoteCancle = true;
            } else {
                participant.updateVote(vote);
            }
        }

        agoraParticipantRepository.save(participant);
        agoraRepository.save(agora); // 변경 감지
        return AgoraReponseDTO.of(agora, isVoteCancle);
    }

    public AgoraDetailReponseDTO createOpinion(Long agoraId, String content, String username) {
        throw new RuntimeException("아직 구현 안됨");
    }
}
