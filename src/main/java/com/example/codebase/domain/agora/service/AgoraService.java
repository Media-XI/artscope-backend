package com.example.codebase.domain.agora.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.agora.dto.*;
import com.example.codebase.domain.agora.entity.*;
import com.example.codebase.domain.agora.repository.AgoraMediaRepository;
import com.example.codebase.domain.agora.repository.AgoraOpinionRepository;
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

    private final AgoraOpinionRepository agoraOpinionRepository;
    private final S3Service s3Service;

    @Autowired
    public AgoraService(MemberRepository memberRepository, AgoraRepository agoraRepository, AgoraParticipantRepository agoraParticipantRepository, AgoraMediaRepository agoraMediaRepository, AgoraOpinionRepository agoraOpinionRepository, S3Service s3Service) {
        this.memberRepository = memberRepository;
        this.agoraRepository = agoraRepository;
        this.agoraParticipantRepository = agoraParticipantRepository;
        this.agoraMediaRepository = agoraMediaRepository;
        this.agoraOpinionRepository = agoraOpinionRepository;
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
        agoraRepository.save(agora);

        // 작성자를 아고라 참여자로 추가
        AgoraParticipant participant = AgoraParticipant.create();
        participant.setAgoraAndMember(agora, member);
        agoraParticipantRepository.save(participant);

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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아고라입니다."));

        agora.isDeleted();

        return AgoraDetailReponseDTO.from(agora);
    }

    @Transactional
    public AgoraReponseDTO updateAgora(Long agoraId, AgoraUpdateDTO dto, String username) {
        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아고라입니다.")); // Agora 1번 1000번지

        if (!agora.isAuthor(username)) {
            throw new RuntimeException("아고라의 작성자가 아닙니다.");
        }

        if (agora.getParticipantsSize() > 1) {
            throw new RuntimeException("아고라에 참가한 사람이 있습니다.");
        }

        agora.update(dto);

        return AgoraReponseDTO.from(agora);
    }

    @Transactional
    public void deleteAgora(Long agoraId, String username) {
        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아고라입니다."));

        if (!agora.isAuthorUsername(username)) {
            throw new RuntimeException("아고라의 작성자가 아닙니다.");
        }

        if (agora.getOpinionSize() > 0) {
            throw new RuntimeException("아고라에 달린 의견이 있습니다.");
        }

        // 정적 이미지 파일(S3) 삭제
        for (AgoraMedia media : agora.getMedias()) {
            media.delete();
            s3Service.deleteObject(media.getMediaUrl());
            agoraMediaRepository.delete(media);
        }

        agora.delete();
        agoraRepository.save(agora);
    }


    @Transactional
    public AgoraReponseDTO voteAgora(Long agoraId, String vote, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아고라입니다."));

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

    @Transactional
    public AgoraDetailReponseDTO createOpinion(Long agoraId, String content, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 아고라입니다."));

        AgoraParticipant participant = agoraParticipantRepository.findById(AgoraParticipantIds.of(agora, member))
                .orElseThrow(() -> new RuntimeException("아고라에 참여하지 않은 사람은 의견을 작성할 수 없습니다."));

        if (!participant.isVoted()) {
            throw new RuntimeException("투표한 사람만 의견을 작성할 수 있습니다.");
        }

        AgoraOpinion opinion = AgoraOpinion.from(content);
        opinion.setAgoraAndAuthor(agora, participant);

        agoraOpinionRepository.save(opinion);
        return AgoraDetailReponseDTO.from(agora);
    }

    @Transactional
    public AgoraDetailReponseDTO updateOpinion(Long agoraId, Long opinionId, String content, String username) {
        AgoraOpinion opinion = agoraOpinionRepository.findById(opinionId)
                .orElseThrow(() -> new RuntimeException("해당 의견이 존재하지 않습니다."));

        opinion.checkAgoraId(agoraId);
        opinion.checkAuthor(username);

        opinion.update(content);

        agoraOpinionRepository.save(opinion);
        return AgoraDetailReponseDTO.from(opinion.getAgora());
    }

    @Transactional
    public AgoraDetailReponseDTO deleteOpinion(Long agoraId, Long opinionId, String username, boolean isAdmin) {
        AgoraOpinion opinion = agoraOpinionRepository.findById(opinionId)
                .orElseThrow(() -> new RuntimeException("해당 의견이 존재하지 않습니다."));

        opinion.checkAgoraId(agoraId);
        opinion.checkAuthorOrIsAdmin(username, isAdmin);

        opinion.delete();

        agoraOpinionRepository.save(opinion);
        return AgoraDetailReponseDTO.from(opinion.getAgora());
    }
}
