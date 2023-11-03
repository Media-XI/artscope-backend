package com.example.codebase.domain.agoda.service;

import com.example.codebase.domain.agoda.dto.AgoraCreateDTO;
import com.example.codebase.domain.agoda.dto.AgoraReponseDTO;
import com.example.codebase.domain.agoda.entity.Agora;
import com.example.codebase.domain.agoda.entity.AgoraMedia;
import com.example.codebase.domain.agoda.entity.AgoraParticipant;
import com.example.codebase.domain.agoda.repository.AgoraRepository;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgoraService {

    private final MemberRepository memberRepository;

    private final AgoraRepository agoraRepository;

    @Autowired
    public AgoraService(MemberRepository memberRepository, AgoraRepository agoraRepository) {
        this.memberRepository = memberRepository;
        this.agoraRepository = agoraRepository;
    }

    @Transactional
    public AgoraReponseDTO createAgora(AgoraCreateDTO dto, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(NotFoundMemberException::new);

        Agora agora = Agora.of(dto, member);

        // 썸네일 추가
        AgoraMedia thumbnail = AgoraMedia.of(dto.getThumbnail(), agora);

        // 미디어 추가
        dto.getMedias().forEach(media -> {
            AgoraMedia.of(media, agora);
        });

        // 작성자를 아고라 참여자로 추가
        AgoraParticipant participant = AgoraParticipant.of(member, agora);

        // 아고라 저장
        agoraRepository.save(agora);

        return AgoraReponseDTO.from(agora, 0, 0, 0);
    }
}
