package com.example.codebase.domain.agora.service;

import com.example.codebase.controller.dto.PageInfo;
import com.example.codebase.domain.agora.dto.*;
import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraMedia;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
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
        AgoraMedia thumbnail = AgoraMedia.of(dto.getThumbnail(), agora);
        agoraMediaRepository.save(thumbnail);

        // 미디어 추가
        dto.getMedias().forEach(mediaDTO -> {
            AgoraMedia agoraMedia = AgoraMedia.of(mediaDTO, agora);
            agoraMediaRepository.save(agoraMedia);
        });
        agoraRepository.save(agora);

        // 작성자를 아고라 참여자로 추가
        AgoraParticipant participant = AgoraParticipant.of(member, agora);
        agoraParticipantRepository.save(participant);

        return AgoraReponseDTO.of(agora, 0, 0, 0);
    }

    @Transactional(readOnly = true)
    public AgorasResponseDTO getAllAgora(PageRequest pageRequest) {
        Page<Agora> agoras = agoraRepository.findAll(pageRequest);
        PageInfo pageInfo = PageInfo.from(agoras);

        List<AgoraReponseDTO> agoraReponseDTOS = agoras.getContent().stream()
                .map(this::settingAgoraCount)
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
        List<AgoraOpinionResponseDTO> agreeOpinionDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.getAuthor().getVote().equals(agora.getAgreeText()))
                .map(AgoraOpinionResponseDTO::from)
                .collect(Collectors.toList());

        List<AgoraOpinionResponseDTO> disagreeOpinionDTOs = agora.getOpinions().stream()
                .filter(opinion -> opinion.getAuthor().getVote().equals(agora.getDisagreeText()))
                .map(AgoraOpinionResponseDTO::from)
                .collect(Collectors.toList());

        AgoraReponseDTO agoraDTO = settingAgoraCount(agora);
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

        agora.update(dto);

        return settingAgoraCount(agora);
    }

    @Transactional
    public void deleteAgora(Long agoraId, String username) {
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

        // S3 이미지 삭제
        agora.getMedias().forEach(media -> {
            media.delete();
            s3Service.deleteObject(media.getMediaUrl());
        });

        agora.delete();
    }

    @Transactional
    public AgoraReponseDTO settingAgoraCount(Agora agora) {
        Integer participatnCount = agoraParticipantRepository.countByAgora(agora).intValue();
        Integer agreeCount = agoraParticipantRepository.countByAgoraAndVote(agora, agora.getAgreeText()).intValue();
        Integer disagreeCount = participatnCount - agreeCount;
        return AgoraReponseDTO.of(agora, agreeCount, disagreeCount, participatnCount);
    }

    @Transactional
    public AgoraReponseDTO voteAgora(Long agoraId, String vote, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = agoraRepository.findById(agoraId)
                .orElseThrow(() -> {
                    throw new NotFoundException("존재하지 않는 아고라입니다.");
                });

        // 투표 메시지 검증
        if (!agora.getAgreeText().equals(vote) && !agora.getDisagreeText().equals(vote)) {
            throw new RuntimeException("투표 내용이 올바르지 않습니다.");
        }

        AgoraParticipant participant = agoraParticipantRepository.findByMemberAndAgora(member, agora) // 이미 투표한 사람인지 확인
                .orElse(AgoraParticipant.of(member, agora)); // 아니면 새로운 투표자로 등록

        // 투표한 사람이 의견을 작성했다면 투표를 변경, 취소할 수 없다.
        if (participant.getOpinions().size() > 0)
            throw new RuntimeException("이미 의견을 작성한 사람은 투표를 변경할 수 없습니다.");

        // 이미 투표한 사람이고 이전과 동일한 투표 내용이면 투표를 취소한다.
        if (agoraParticipantRepository.existsByMemberAndAgora(member, agora))
            if (participant.getVote().equals(vote)) {
                participant.cancle(vote);
                agoraParticipantRepository.save(participant);
                return settingAgoraCount(agora);
            }

        // 새로운 투표이면 투표를 진행한다.
        int size = agora.getParticipants().size(); // 순번
        participant.vote(vote, size); // 새로운 투표 or 투표 변경 허용

        agoraParticipantRepository.save(participant);
        return settingAgoraCount(agora);
    }
}
