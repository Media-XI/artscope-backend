package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.exhibition.dto.*;
import com.example.codebase.domain.exhibition.entity.*;
import com.example.codebase.domain.exhibition.repository.EventScheduleRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionParticipantRepository;
import com.example.codebase.domain.exhibition.repository.ExhibitionRepository;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
import com.example.codebase.domain.member.entity.RoleStatus;
import com.example.codebase.domain.member.repository.MemberAuthorityRepository;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.transaction.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExhibitionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private EventScheduleRepository eventScheduleRepository;

    @Autowired
    private ExhibitionParticipantRepository exhibitionParticipantRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    public Member createOrLoadMember() {
        return createOrLoadMember("testid", RoleStatus.CURATOR, "ROLE_CURATOR");
    }

    public Member createOrLoadMember(String username, RoleStatus roleStatus, String... authorities) {
        Optional<Member> testMember = memberRepository.findByUsername(username);
        if (testMember.isPresent()) {
            return testMember.get();
        }

        Member dummy =
                Member.builder()
                        .username(username)
                        .password(passwordEncoder.encode("1234"))
                        .email(username + "@test.com")
                        .name(username)
                        .activated(true)
                        .createdTime(LocalDateTime.now())
                        .roleStatus(roleStatus)
                        .build();

        for (String authority : authorities) {
            MemberAuthority memberAuthority = new MemberAuthority();
            memberAuthority.setAuthority(Authority.of(authority));
            memberAuthority.setMember(dummy);
            dummy.addAuthority(memberAuthority);
        }

        Member save = memberRepository.save(dummy);
        return save;
    }

    public Exhibition createOrLoadExhibition() {
        return createOrLoadExhibition(1);
    }

    public Exhibition createOrLoadExhibition(int idx) {
        return createOrLoadExhibition(idx, LocalDate.now(), LocalDate.now().plusWeeks(1), 1);
    }

    public Exhibition createOrLoadExhibition(int idx, LocalDate startDate) {
        return createOrLoadExhibition(idx, startDate, startDate.plusWeeks(1), 1);
    }

    public Exhibition createOrLoadExhibition(int idx, LocalDate startDate, int scheduleSize) {
        return createOrLoadExhibition(idx, startDate, startDate.plusWeeks(1), scheduleSize);
    }


    @Transactional
    public Exhibition createOrLoadExhibition(
            int idx, LocalDate startDate, LocalDate endDate, int scheduleSize) {
        Optional<Exhibition> save = exhibitionRepository.findById(Long.valueOf(idx));
        if (save.isPresent()) {
            return save.get();
        }

        Exhibition exhibition =
                Exhibition.builder()
                        .title("이벤트 제목" + idx)
                        .description("이벤트 설명" + idx)
                        .link("링크" + idx)
                        .price("10000원"+idx)
                        .type(EventType.STANDARD)
                        .createdTime(LocalDateTime.now())
                        .member(createOrLoadMember())
                        .build();

        ExhibitionMedia thumbnail =
                ExhibitionMedia.builder()
                        .mediaUrl("url" + idx)
                        .exhibtionMediaType(ExhibtionMediaType.image)
                        .createdTime(LocalDateTime.now())
                        .build();
        exhibition.addExhibitionMedia(thumbnail);

        ExhibitionMedia media =
                ExhibitionMedia.builder()
                        .mediaUrl("url" + idx)
                        .exhibtionMediaType(ExhibtionMediaType.image)
                        .createdTime(LocalDateTime.now())
                        .build();
        exhibition.addExhibitionMedia(media);

        exhibitionRepository.save(exhibition);

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

        for (int i = 0; i < scheduleSize; i++) {
            LocalDateTime defaultStartDateTime = LocalDateTime.now()
                    .withSecond(0)
                    .withNano(0);

            long time = System.currentTimeMillis();

            EventSchedule eventSchedule =
                    EventSchedule.builder()
                            .startDateTime(defaultStartDateTime.plusDays(i))
                            .endDateTime(defaultStartDateTime.plusDays(i).plusHours(2))
                            .detailLocation("상세 위치")
                            .createdTime(LocalDateTime.now())
                            .build();
            eventSchedule.setEvent(exhibition);
            eventSchedule.setLocation(location);

            ExhibitionParticipant exhibitionParticipant =
                    ExhibitionParticipant.builder().member(createOrLoadMember()).build();
            exhibitionParticipant.setEventSchedule(eventSchedule);
            exhibitionParticipantRepository.save(exhibitionParticipant);

            eventScheduleRepository.save(eventSchedule);
        }

        return exhibition;
    }

    public Location createMockLocation() {
        // Localtion
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
        return locationRepository.save(location);
    }

    public ExhbitionCreateDTO mockCreateExhibitionDTO() {
        return mockCreateExhibitionDTO(1);
    }

    public ExhbitionCreateDTO mockCreateExhibitionDTO(int scheduleSize) {
        ExhibitionMediaCreateDTO thumbnailDTO = new ExhibitionMediaCreateDTO();
        thumbnailDTO.setMediaType(ExhibtionMediaType.image.name());
        thumbnailDTO.setMediaUrl("http://localhost/");

        ExhibitionMediaCreateDTO mediaCreateDTO = new ExhibitionMediaCreateDTO();
        mediaCreateDTO.setMediaType(ExhibtionMediaType.image.name());
        mediaCreateDTO.setMediaUrl("http://localhost/");

        List<EventScheduleCreateDTO> scuheduleDTOs = new ArrayList<>();
        for (int i = 0; i < scheduleSize; i++) {
            EventScheduleCreateDTO scuheduleDTO = new EventScheduleCreateDTO();
            scuheduleDTO.setStartDateTime(LocalDateTime.now().plusMinutes(i));
            scuheduleDTO.setEndDateTime(LocalDateTime.now().plusHours(2).plusMinutes(i));
            scuheduleDTO.setLocationId(createMockLocation().getId());
            scuheduleDTO.setDetailLocation("상세 위치" + i);
            scuheduleDTOs.add(scuheduleDTO);
        }

        ExhbitionCreateDTO dto = new ExhbitionCreateDTO();
        dto.setTitle("이벤트 제목");
        dto.setDescription("이벤트 설명");
        dto.setPrice("10000원");
        dto.setLink("http://event.com");
        dto.setEventType(EventType.STANDARD);
        dto.setSchedule(scuheduleDTOs);
        dto.setMedias(Collections.singletonList(mediaCreateDTO));
        dto.setThumbnail(thumbnailDTO);

        return dto;
    }

    private byte[] createImageFile() throws IOException {
        File file =
                resourceLoader.getResource("classpath:test/img.jpg").getFile();
        return Files.readAllBytes(file.toPath());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("이벤트 등록")
    @Test
    public void 이벤트_등록() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO();

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("스케줄이 없는 이벤트 등록 시")
    @Test
    public void 스케줄이_없는_이벤트_등록() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO(0);

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("스케줄이 5개인 이벤트 등록 시")
    @Test
    public void 스케줄이_5개인_이벤트_등록() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO(5);

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @WithMockCustomUser(username = "admin", role = "CURATOR")
    @DisplayName("스케줄 시작시간이 종료시간보다 빠를시")
    @Test
    public void 스케줄이_시작시간이_더_빠른경우_이벤트등록() throws Exception {
        createOrLoadMember("admin", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO(1);
        EventScheduleCreateDTO eventScheduleCreateDTO = dto.getSchedule().get(0);
        eventScheduleCreateDTO.setStartDateTime(LocalDateTime.now());
        eventScheduleCreateDTO.setEndDateTime(LocalDateTime.now().minusHours(1));

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("이벤트 전체 조회 - 성공")
    @Test
    public void 이벤트를_전체_조회합니다() throws Exception {
        createOrLoadExhibition(1, LocalDate.now());
        createOrLoadExhibition(2, LocalDate.now().plusWeeks(1));
        createOrLoadExhibition(3, LocalDate.now().plusDays(1));
        createOrLoadExhibition(4, LocalDate.now().minusDays(1), 2);
        createOrLoadExhibition(5, LocalDate.now().plusWeeks(2));
        createOrLoadExhibition(6, LocalDate.now());

        ExhibitionSearchDTO exhibitionSearchDTO =
                ExhibitionSearchDTO.builder()
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusMonths(3))
                        .build();

        int page = 0;
        int size = 10;
        String sortDirection = "DESC";

        mockMvc
                .perform(
                        get("/api/exhibitions")
                                .param("startDate", exhibitionSearchDTO.getStartDate().toString())
                                .param("endDate", exhibitionSearchDTO.getEndDate().toString())
                                .param("eventType", "ALL")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("sortDirection", sortDirection))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "CURATOR")
    @DisplayName("공모전 수정")
    @Test
    public void 공모전_수정() throws Exception {
        createOrLoadMember("testid", RoleStatus.CURATOR, "ROLE_CURATOR");
        Exhibition exhibition = createOrLoadExhibition();

        ExhibitionUpdateDTO dto = new ExhibitionUpdateDTO();
        dto.setTitle("수정된 제목");
        dto.setDescription("수정된 설명 꽁자");

        mockMvc
                .perform(
                        put(String.format("/api/exhibitions/%d", exhibition.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("작성자가 아닐 시 이벤트 수정")
    @Test
    public void 작성자가_아닌_유저가_공모전_수정() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");
        Exhibition exhibition = createOrLoadExhibition(); // testid 사용자가 만듬

        ExhibitionUpdateDTO dto = new ExhibitionUpdateDTO();
        dto.setTitle("수정된 제목");
        dto.setDescription("수정된 설명");
        dto.setPrice("3200원");

        mockMvc
                .perform(
                        put(String.format("/api/exhibitions/%d", exhibition.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "CURATOR")
    @DisplayName("공모전 삭제")
    @Test
    public void 공모전_삭제() throws Exception {
        Exhibition exhibition = createOrLoadExhibition();

        mockMvc
                .perform(delete(String.format("/api/exhibitions/%d", exhibition.getId())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("공모전 전체 조회 - 시작일과 종료일이 같을떄")
    @Test
    public void 공모전_전체_조회() throws Exception {
        createOrLoadExhibition(1, LocalDate.now());
        createOrLoadExhibition(2, LocalDate.now().minusDays(1));
        createOrLoadExhibition(3, LocalDate.now().plusDays(2));

        ExhibitionSearchDTO exhibitionSearchDTO =
                ExhibitionSearchDTO.builder()
                        .startDate(LocalDate.now().minusWeeks(1))
                        .endDate(LocalDate.now().plusMonths(1))
                        .eventType(SearchEventType.ALL.name())
                        .build();

        int page = 0;
        int size = 10;
        String sortDirection = "DESC";

        mockMvc
                .perform(
                        get("/api/exhibitions")
                                .param("startDate", exhibitionSearchDTO.getStartDate().toString())
                                .param("endDate", exhibitionSearchDTO.getEndDate().toString())
                                .param("eventType", exhibitionSearchDTO.getEventType())
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("sortDirection", sortDirection))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("공모전 전체 조회 - 시작일이 종료일보다 빠를때")
    @Test
    public void 공모전_전체_조회시_시작일이_종료일보다_빠를때() throws Exception {
        createOrLoadExhibition(1);
        createOrLoadExhibition(2);
        createOrLoadExhibition(3);

        ExhibitionSearchDTO exhibitionSearchDTO =
                ExhibitionSearchDTO.builder()
                        .startDate(LocalDate.now().plusDays(1))
                        .endDate(LocalDate.now())
                        .eventType(EventType.STANDARD.name())
                        .build();
        int page = 0;
        int size = 10;
        String sortDirection = "DESC";

        mockMvc
                .perform(
                        get("/api/exhibitions")
                                .param("startDate", exhibitionSearchDTO.getStartDate().toString())
                                .param("endDate", exhibitionSearchDTO.getEndDate().toString())
                                .param("eventType", exhibitionSearchDTO.getEventType())
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("sortDirection", sortDirection))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("공모전 상세 조회")
    @Test
    public void 공모전_상세_조회() throws Exception {
        Exhibition exhibition = createOrLoadExhibition(3, LocalDate.now(), 3);

        mockMvc
                .perform(get("/api/exhibitions/{exhibitionId}", exhibition.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("스케줄 시작시간과 종료시간이 같을시")
    @Test
    public void 스케줄시간과_종료시간이_같은경우_이벤트등록() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO(1);
        EventScheduleCreateDTO eventScheduleCreateDTO = dto.getSchedule().get(0);
        eventScheduleCreateDTO.setEndDateTime(LocalDateTime.now());
        eventScheduleCreateDTO.setStartDateTime(LocalDateTime.now());

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("권한 없는 유저가 이벤트를 생성할시")
    @Test
    public void 권한이_없는_유저가_수정_할떄() throws Exception {
        createOrLoadMember("user", RoleStatus.NONE, "ROLE_USER");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO();

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("스케줄 종료시간이 없을시")
    @Test
    public void 스케줄_생성시_종료시간이_없는_경우() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO(1);
        EventScheduleCreateDTO eventScheduleCreateDTO = dto.getSchedule().get(0);
        eventScheduleCreateDTO.setEndDateTime(null);
        eventScheduleCreateDTO.setStartDateTime(LocalDateTime.now());

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated());
    }


    @WithMockCustomUser(username = "testid", role = "ROLE_CURATOR")
    @DisplayName("이벤트 스케줄 삭제시")
    @Test
    public void 이벤트_스케줄_삭제시() throws Exception {
        Member member = createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        Exhibition exhbition = createOrLoadExhibition(1, LocalDate.now(), 10);
        EventSchedule eventSchedule = exhbition.getFirstEventSchedule();

        mockMvc
                .perform(delete(String.format("/api/exhibitions/%d/schedule/%d", exhbition.getId(), eventSchedule.getId())))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("이벤트 권한이 없는 유저가 이벤트 스케줄 삭제시")
    @Test
    public void 이벤트_권한이_없는_유저가_이벤트_스케줄_삭제시() throws Exception {

        Exhibition exhbition = createOrLoadExhibition(1, LocalDate.now());
        EventSchedule eventSchedule = exhbition.getFirstEventSchedule();

        mockMvc
                .perform(delete(String.format("/api/exhibitions/%d/schedule/%d", exhbition.getId(), eventSchedule.getId())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("스케줄 종료시간이 시작시간보다 빠를시")
    @Test
    public void 스케줄_생성시_종료시간이_시작시간보다_빠른경우() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        ExhbitionCreateDTO dto = mockCreateExhibitionDTO(1);
        EventScheduleCreateDTO eventScheduleCreateDTO = dto.getSchedule().get(0);
        eventScheduleCreateDTO.setEndDateTime(LocalDateTime.now().minusHours(1));
        eventScheduleCreateDTO.setStartDateTime(LocalDateTime.now());

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/exhibitions")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
