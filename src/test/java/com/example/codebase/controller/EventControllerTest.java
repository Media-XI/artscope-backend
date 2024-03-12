package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.event.dto.*;
import com.example.codebase.domain.event.entity.*;
import com.example.codebase.domain.event.repository.EventRepository;
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
import jakarta.transaction.Transactional;
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
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

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

        return memberRepository.save(dummy);
    }

    private byte[] createImageFile() throws IOException {
        File file =
                resourceLoader.getResource("classpath:test/img.jpg").getFile();
        return Files.readAllBytes(file.toPath());
    }

    public Location createMockLocation() {
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

    public EventCreateDTO mockCreateEventDTO(int idx) {
        EventMediaCreateDTO thumbnailDTO = new EventMediaCreateDTO();
        thumbnailDTO.setMediaType(EventMediaType.image.name());
        thumbnailDTO.setMediaUrl("http://localhost/");

        EventMediaCreateDTO mediaCreateDTO = new EventMediaCreateDTO();
        mediaCreateDTO.setMediaType(EventMediaType.image.name());
        mediaCreateDTO.setMediaUrl("http://localhost/");

        EventCreateDTO dto = new EventCreateDTO();
        dto.setTitle("test" + idx);
        dto.setDescription("test description" + idx);
        dto.setPrice("10000원 ~ 20000원" + idx);
        dto.setLink("http://localhost/");
        dto.setEventType(EventType.WORKSHOP);
        dto.setStartDate(LocalDate.now().plusDays(idx));
        dto.setEndDate(LocalDate.now().plusDays(1).plusDays(idx));
        dto.setDetailedSchedule("14시 ~ 16시");
        dto.setLocationId(createMockLocation().getId());
        dto.setDetailLocation("2층");
        dto.setThumbnail(thumbnailDTO);
        dto.setMedias(Collections.singletonList(mediaCreateDTO));

        return dto;
    }

    public Event createOrLoadEvent() {
        return createOrLoadEvent(1, LocalDate.now());
    }

    public Event createOrLoadEvent(int idx, LocalDate startDate) {
        return createOrLoadEvent(idx, startDate, startDate);
    }

    @Transactional
    public Event createOrLoadEvent(int idx, LocalDate startDate, LocalDate endDate) {

        Member member = createOrLoadMember();

        EventMedia thumbnail = EventMedia.builder()
                .mediaUrl("url")
                .eventMediaType(EventMediaType.image)
                .createdTime(LocalDateTime.now())
                .build();

        EventMedia media = EventMedia.builder()
                .mediaUrl("url")
                .eventMediaType(EventMediaType.image)
                .createdTime(LocalDateTime.now())
                .build();

        Location location = createMockLocation();

        Event event = Event.builder()
                .title("test" + idx)
                .description("test description" + idx)
                .price("10000원 ~ 20000원" + idx)
                .link("http://localhost/")
                .type(EventType.WORKSHOP)
                .startDate(startDate.plusDays(idx))
                .endDate(endDate.plusDays(idx))
                .detailedSchedule("14시 ~ 16시")
                .detailLocation("2층")
                .location(location)
                .eventMedias(new ArrayList<>(List.of(thumbnail, media)))
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .member(member)
                .detailLocation("2층")
                .build();

        eventRepository.save(event);
        return event;
    }

    @WithMockCustomUser(username = "user", role = "CURATOR")
    @DisplayName("이벤트 등록")
    @Test
    public void 이벤트_생성() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        EventCreateDTO dto = mockCreateEventDTO(0);

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/events")
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
    @DisplayName("일정이 없는 이벤트 등록시")
    @Test
    public void 일정이_없는_이벤트_등록() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        EventCreateDTO dto = mockCreateEventDTO(0);
        dto.setStartDate(null);
        dto.setEndDate(null);

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/events")
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
    @DisplayName("시작과 종료 날이 같은 이벤트 등록시")
    @Test
    public void 시작과_종료_날이_같은_이벤트_등록시() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        EventCreateDTO dto = mockCreateEventDTO(0);
        dto.setEndDate(dto.getStartDate());

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/events")
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
    @DisplayName("권한 없는 유저가 이벤트를 등록할시")
    @Test
    public void 권한이_없는_유저가_이벤트를_등록할시() throws Exception{
        createOrLoadMember("user", RoleStatus.NONE, "ROLE_USER");

        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        EventCreateDTO dto = mockCreateEventDTO(0);
        dto.setEndDate(dto.getStartDate());

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/events")
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
    @DisplayName("종료일이 시작일 보다 빠른 이벤트 등록시")
    @Test
    public void 종료일이_시작일_보다_빠른_이벤트_등록시() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");

        EventCreateDTO dto = mockCreateEventDTO(0);
        dto.setEndDate(dto.getStartDate().minusDays(1));

        MockMultipartFile dtoFile =
                new MockMultipartFile("dto", "", "application/json", objectMapper.writeValueAsBytes(dto));

        MockMultipartFile mediaFile =
                new MockMultipartFile("mediaFiles", "image.jpg", "image/jpg", createImageFile());

        MockMultipartFile thumbnailFile =
                new MockMultipartFile("thumbnailFile", "image.jpg", "image/jpg", createImageFile());

        mockMvc
                .perform(
                        multipart("/api/events")
                                .file(dtoFile)
                                .file(mediaFile)
                                .file(thumbnailFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("이벤트 목록 조회 - 성공")
    @Test
    public void 이벤트를_전체_조회합니다() throws Exception {
        createOrLoadEvent(1, LocalDate.now());
        createOrLoadEvent(2, LocalDate.now().plusWeeks(1));
        createOrLoadEvent(3, LocalDate.now().plusDays(1));
        createOrLoadEvent(4, LocalDate.now().minusDays(1));
        createOrLoadEvent(5, LocalDate.now().minusWeeks(1));
        createOrLoadEvent(6, LocalDate.now().minusMonths(1));

        EventSearchDTO eventSearchDTO = new EventSearchDTO();
        eventSearchDTO.setEventType("ALL");

        int page = 0;
        int size = 10;
        String sortDirection = "DESC";

        mockMvc
                .perform(
                        get("/api/events")
                                .param("startDate", eventSearchDTO.getStartDate().toString())
                                .param("endDate", eventSearchDTO.getEndDate().toString())
                                .param("eventType", "ALL")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("sortDirection", sortDirection))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("이벤트 목록 시작일 종료일 지정 조회 - 성공")
    @Test
    public void 이벤트_특정_시간_사이를_조회합니다() throws Exception {
        createOrLoadEvent(1, LocalDate.now());
        createOrLoadEvent(2, LocalDate.now().plusWeeks(1));
        createOrLoadEvent(3, LocalDate.now().plusDays(1));
        createOrLoadEvent(4, LocalDate.now().minusDays(1));
        createOrLoadEvent(5, LocalDate.now().minusWeeks(1));
        createOrLoadEvent(6, LocalDate.now().minusMonths(1));

        EventSearchDTO eventSearchDTO = new EventSearchDTO();
        eventSearchDTO.setStartDate(LocalDate.now().minusDays(2));
        eventSearchDTO.setEndDate(LocalDate.now().plusDays(2));
        eventSearchDTO.setEventType("ALL");

        int page = 0;
        int size = 10;
        String sortDirection = "DESC";

        mockMvc
                .perform(
                        get("/api/events")
                                .param("startDate", eventSearchDTO.getStartDate().toString())
                                .param("endDate", eventSearchDTO.getEndDate().toString())
                                .param("eventType", "ALL")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size))
                                .param("sortDirection", sortDirection))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("이벤트 수정")
    @Test
    public void 이벤트_수정() throws Exception {
        createOrLoadMember("testid", RoleStatus.CURATOR, "ROLE_CURATOR");
        Event event = createOrLoadEvent();

        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setTitle("수정된 제목");
        dto.setDescription("수정된 설명 공지");

        mockMvc
                .perform(
                        put(String.format("/api/events/%d", event.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "user12", role = "USER")
    @DisplayName("작성자가 아닌 유저가 이벤트 수정")
    @Test
    public void 작성자가_아닌_유저가_이벤트_수정() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_USER");
        Event event = createOrLoadEvent();

        EventUpdateDTO dto = new EventUpdateDTO();
        dto.setTitle("수정된 제목");
        dto.setDescription("수정된 설명 공지");

        mockMvc
                .perform(
                        put(String.format("/api/events/%d", event.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("이벤트 삭제")
    @Test
    public void 이벤트_삭제() throws Exception {
        createOrLoadMember("testid", RoleStatus.CURATOR, "ROLE_CURATOR");
        Event event = createOrLoadEvent();

        mockMvc
                .perform(delete(String.format("/api/events/%d", event.getId())))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "user", role = "USER")
    @DisplayName("작성자가 아닌 유저가 이벤트 삭제")
    @Test
    public void 작성자가_아닌_유저가_이벤트_삭제() throws Exception {
        createOrLoadMember("user", RoleStatus.CURATOR, "ROLE_CURATOR");
        Event event = createOrLoadEvent();

        mockMvc
                .perform(delete(String.format("/api/events/%d", event.getId())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
