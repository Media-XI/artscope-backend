package com.example.codebase.controller;

import com.example.codebase.domain.event.repository.EventRepository;
import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.agora.entity.AgoraParticipant;
import com.example.codebase.domain.agora.repository.AgoraParticipantRepository;
import com.example.codebase.domain.agora.repository.AgoraRepository;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.artwork.repository.ArtworkRepository;
import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.event.entity.*;
import com.example.codebase.domain.event.repository.ExhibitionParticipantRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.media.MediaType;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.domain.post.entity.Post;
import com.example.codebase.domain.post.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ExhibitionParticipantRepository exhibitionParticipantRepository;

    @Autowired
    private AgoraRepository agoraRepository;

    @Autowired
    private AgoraParticipantRepository agoraParticipantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Transactional
    public Member createOrLoadMember() {
        Optional<Member> testMember = memberRepository.findByUsername("testid");
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy = Member.builder()
                .username("testid")
                .password(passwordEncoder.encode("1234"))
                .email("email")
                .name("test")
                .activated(true)
                .createdTime(LocalDateTime.now())
                .build();

        MemberAuthority memberAuthority = new MemberAuthority();
        memberAuthority.setAuthority(Authority.of("ROLE_USER"));
        memberAuthority.setMember(dummy);
        dummy.addAuthority(memberAuthority);

        Member save = memberRepository.save(dummy);
        memberAuthorityRepository.save(memberAuthority);
        return save;
    }

    @Transactional
    public Artwork createOrLoadArtwork(int index, boolean isVisible, int mediaSize) throws IOException {
        Optional<Artwork> artwork = artworkRepository.findById(Long.valueOf(index));
        if (artwork.isPresent()) {
            return artwork.get();
        }

        List<ArtworkMedia> artworkMediaList = new ArrayList<>();
        for (int i = 0; i < mediaSize; i++) {
            String url = "https://test.com/image.jpg";

            ArtworkMedia artworkMedia = ArtworkMedia.builder()
                    .artworkMediaType(MediaType.image)
                    .mediaUrl(url)
                    .description("미디어 설명")
                    .build();
            artworkMediaList.add(artworkMedia);
        }

        Artwork dummy = Artwork.builder()
                .title("아트워크_테스트" + index)
                .description("작품 설명")
                .tags("태그1,태그2,태그3")
                .visible(isVisible)
                .member(createOrLoadMember())
                .artworkMedia(artworkMediaList)
                .createdTime(LocalDateTime.now().plusSeconds(index))
                .build();

        return artworkRepository.save(dummy);
    }

    @Transactional
    public Post createPost() {
        Member loadMember = createOrLoadMember();

        Post post = Post.builder()
                .content("content")
                .author(loadMember)
                .createdTime(LocalDateTime.now())
                .build();
        return postRepository.save(post);
    }

    @Transactional
    public Event createOrLoadExhibition(int idx) {
        Optional<Event> save = eventRepository.findById((long) idx);
        if (save.isPresent()) {
            return save.get();
        }

        Location location =
                Location.builder()
                        .latitude(123.123)
                        .longitude(123.123)
                        .address("주소")
                        .name("장소 이름")
                        .englishName("장소 영어 이름")
                        .phoneNumber("010-1234-1234")
                        .webSiteUrl("test.com")
                        .snsUrl("test.com")
                        .build();
        locationRepository.save(location);


        Event event = Event.builder()
                .title("test" + idx)
                .description("test description" + idx)
                .price("10000원 ~ 20000원" + idx)
                .link("http://localhost/")
                .type(EventType.WORKSHOP)
                .startDate(LocalDate.now().plusDays(idx))
                .endDate(LocalDate.now().plusDays(idx + 1))
                .detailedSchedule("14시 ~ 16시")
                .detailLocation("2층")
                .location(location)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .member(createOrLoadMember())
                .detailLocation("2층")
                .build();

        EventMedia thumbnail =
                EventMedia.builder()
                        .mediaUrl("url" + idx)
                        .eventMediaType(EventMediaType.image)
                        .createdTime(LocalDateTime.now())
                        .build();
        event.addEventMedia(thumbnail);


        EventMedia media =
                EventMedia.builder()
                        .mediaUrl("url" + idx)
                        .eventMediaType(EventMediaType.image)
                        .createdTime(LocalDateTime.now())
                        .build();
        event.addEventMedia(media);

        Event saveEvent = eventRepository.save(event);
        return saveEvent;
    }

    @Transactional
    public Agora createAgora(int idx, boolean isAnnoymous) {
        Optional<Agora> agora = agoraRepository.findById(Long.valueOf(idx));
        if (agora.isPresent()) {
            return agora.get();
        }

        Member member = createOrLoadMember();

        Agora dummy = Agora.builder()
                .title("아고라_테스트" + idx)
                .content("아고라_테스트_내용" + idx)
                .agreeText("찬성")
                .disagreeText("반대")
                .naturalText("중립")
                .agreeCount(0)
                .disagreeCount(0)
                .naturalCount(0)
                .isAnonymous(isAnnoymous)
                .author(member)
                .createdTime(LocalDateTime.now())
                .build();
        agoraRepository.save(dummy);

        AgoraParticipant agoraParticipant = AgoraParticipant.create();
        agoraParticipant.setAgoraAndMember(dummy, member);
        agoraParticipantRepository.save(agoraParticipant);

        return dummy;
    }

    @Transactional
    public void voteAgora(Agora agora, Member member, String voteText) {
        AgoraParticipant agoraParticipant = AgoraParticipant.create();
        agoraParticipant.setAgoraAndMember(agora, member);
        agoraParticipant.createVote(voteText);
        agoraParticipantRepository.save(agoraParticipant);
    }

    @DisplayName("피드 생성")
    @Test
    public void createFeed() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);
        createOrLoadArtwork(3, true, 1);
        createOrLoadArtwork(4, true, 1);
        createOrLoadArtwork(5, true, 1);
        createOrLoadArtwork(6, true, 1);
        createOrLoadArtwork(7, true, 1);
        createOrLoadArtwork(8, true, 1);
        createOrLoadArtwork(9, true, 1);
        createOrLoadArtwork(10, true, 1);

        // Post 생성 및 저장
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();
        createPost();

        // 전시 생성 및 저장
        createOrLoadExhibition(1);
        createOrLoadExhibition(2);
        createOrLoadExhibition(3);
        createOrLoadExhibition(4);

        // Agora
        createAgora(1, false);
        createAgora(2, true);
        createAgora(3, true);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성2")
    @Test
    public void createFeed_Agora() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);

        // Post 생성 및 저장
        createPost();
        createPost();

        // 전시 생성 및 저장
        createOrLoadExhibition(1);
        createOrLoadExhibition(2);

        // Agora
        createAgora(1, false);
        createAgora(2, true);
        createAgora(3, true);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성 시 일부 데이터 없을 떄 ")
    @Test
    public void createFeed_일부데이터없이() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);
        createOrLoadArtwork(3, true, 1);
        createOrLoadArtwork(4, true, 1);
        createOrLoadArtwork(5, true, 1);
        createOrLoadArtwork(6, true, 1);
        createOrLoadArtwork(7, true, 1);
        createOrLoadArtwork(8, true, 1);
        createOrLoadArtwork(9, true, 1);
        createOrLoadArtwork(10, true, 1);

        // Post 생성 및 저장
        createPost();
        createPost();

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "1")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성 시 일부 데이터가 없다면 ")
    @Test
    public void createFeed2() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);

        // 전시 생성 및 저장
        createOrLoadExhibition(1);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 생성 시 전체 데이터가 없다면 ")
    @Test
    public void createFeed3() throws Exception {

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @DisplayName("피드 조회 시 page가 0이면 에러 발생")
    @Test
    public void createFeed4() throws Exception {

        // 아트워크 생성 및 저장
        createOrLoadArtwork(1, true, 1);

        // Post 생성 및 저장
        createPost();

        // 전시 생성 및 저장
        createOrLoadExhibition(1);

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("일부 포스트, 아트워크 좋아요 여부에 따른 피드 조회")
    @Test
    public void createFeed5() throws Exception {
        Artwork artwork = createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);

        // Post 생성 및 저장
        Post post = createPost();
        createPost();

        createOrLoadExhibition(1);

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/artworks/" + artwork.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("일부 아고라 투표 여부에 따른 피드 조회")
    @Test
    public void createFeed6() throws Exception {
        Member member = createOrLoadMember();
        Artwork artwork = createOrLoadArtwork(1, true, 1);
        createOrLoadArtwork(2, true, 1);

        // Post 생성 및 저장
        Post post = createPost();
        createPost();

        createOrLoadExhibition(1);

        // Agora
        Agora agora1 = createAgora(1, false);
        createAgora(2, true);
        createAgora(3, true);

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/artworks/" + artwork.getId() + "/like")
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/agoras/" + agora1.getId() + "/vote")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(agora1.getAgreeText())
                )
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/feed")
                                .param("page", "0")
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }
}