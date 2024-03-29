package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.event.entity.Event;
import com.example.codebase.domain.event.entity.EventMedia;
import com.example.codebase.domain.event.entity.EventMediaType;
import com.example.codebase.domain.event.entity.EventType;
import com.example.codebase.domain.event.repository.EventRepository;
import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.dto.LocationUpdateDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class LocationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    public Member createOrLoadMember() {
        return createOrLoadMember("testid", RoleStatus.CURATOR, "ROLE_ADMIN");
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

    @Transactional
    public Event createOrLoadEvent(Location location) {

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

        Event event = Event.builder()
                .title("test")
                .description("test description")
                .price("10000원 ~ 20000원")
                .link("http://localhost/")
                .type(EventType.WORKSHOP)
                .startDate(LocalDate.now())
                .location(location)
                .endDate(LocalDate.now().plusDays(1))
                .detailedSchedule("14시 ~ 16시")
                .detailLocation("2층")
                .location(location)
                .eventMedias(new ArrayList<>(List.of(thumbnail, media)))
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .member(member)
                .detailLocation("2층")
                .build();

        location.addEvent(event);
        eventRepository.save(event);
        return event;
    }

    public Location createOrLoadLocation() {
        return createOrLoadLocation(1, createOrLoadMember());
    }

    public Location createOrLoadLocation(int index) {
        return createOrLoadLocation(index, createOrLoadMember());
    }

    public Location createOrLoadLocation(int index, Member member) {
        Location location =
                Location.builder()
                        .latitude(37.123456 + index)
                        .longitude(127.123456 + index)
                        .address("경기도 용인시 수지구 죽전동" + index)
                        .name("테스트 장소" + index)
                        .englishName("Test Location" + index)
                        .phoneNumber("010-1234-5678" + index)
                        .webSiteUrl("https://test.com" + index)
                        .snsUrl("https://test.com" + index)
                        .member(member)
                        .build();

        return locationRepository.save(location);
    }

    public Event createOrLoadEvent() {
        return createOrLoadEvent();
    }

    public LocationCreateDTO createOrLoadLocationCreateDTO() {
        LocationCreateDTO locationCreateDTO = new LocationCreateDTO();
        locationCreateDTO.setLatitude(37.123456);
        locationCreateDTO.setLongitude(127.123456);
        locationCreateDTO.setAddress("경기도 용인시 수지구 죽전동");
        locationCreateDTO.setName("테스트 장소");
        locationCreateDTO.setEnglishName("Test Location");
        locationCreateDTO.setPhoneNumber("010-1234-5678");
        locationCreateDTO.setWebSiteUrl("https://test.com");
        locationCreateDTO.setSnsUrl("https://test.com");

        return locationCreateDTO;
    }

    public LocationUpdateDTO locationUpdateDTO() {
        LocationUpdateDTO locationUpdateDTO = new LocationUpdateDTO();
        locationUpdateDTO.setLatitude(50.123456);
        locationUpdateDTO.setLongitude(-123.123456);
        locationUpdateDTO.setAddress("경기도 수정구 수정한동");
        locationUpdateDTO.setName("수정 장소");
        locationUpdateDTO.setEnglishName("Update Location");
        locationUpdateDTO.setPhoneNumber("010-1234-5678");
        locationUpdateDTO.setWebSiteUrl("https://update.com");
        locationUpdateDTO.setSnsUrl("https://update.com");

        return locationUpdateDTO;
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("장소 생성 테스트")
    @Test
    public void 장소_등록() throws Exception {
        createOrLoadMember();

        LocationCreateDTO locationCreateDTO = createOrLoadLocationCreateDTO();

        mockMvc
                .perform(
                        post("/api/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(locationCreateDTO)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("특정 장소 상세 조회 테스트")
    @Test
    public void 특정_장소_상세조회() throws Exception {
        Location location = createOrLoadLocation();

        mockMvc
                .perform(get("/api/location/{locationId}", location.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("이름으로 장소 검색 테스트")
    @Test
    public void 이름으로_장소_검색() throws Exception {
        createOrLoadLocation(1);
        createOrLoadLocation(2);
        Location location = createOrLoadLocation(3);
        createOrLoadLocation(4);
        createOrLoadLocation(5);

        int page = 0;
        int size = 10;

        mockMvc
                .perform(
                        get("/api/location/search")
                                .param("keyword", location.getName())
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("주소로 장소 검색 테스트")
    @Test
    public void 주소로_장소_검색() throws Exception {
        createOrLoadLocation(1);
        createOrLoadLocation(2);
        createOrLoadLocation(3);
        createOrLoadLocation(4);
        createOrLoadLocation(5);

        int page = 0;
        int size = 10;

        mockMvc
                .perform(
                        get("/api/location/search")
                                .param("keyword", "경기도")
                                .param("page", String.valueOf(page))
                                .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("인증안한 유저가 장소 등록을 시도할 시")
    @Test
    public void 인증_안된_유저가_장소_등록() throws Exception {
        createOrLoadMember("testid", RoleStatus.NONE);

        LocationCreateDTO locationCreateDTO = createOrLoadLocationCreateDTO();

        mockMvc
                .perform(
                        post("/api/location")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(locationCreateDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("관리자가 장소를 수정할 시")
    @Test
    public void 인증된_유저가_장소_수정_시() throws Exception {
        Location location = createOrLoadLocation();

        LocationUpdateDTO locationUpdateDTO = locationUpdateDTO();

        mockMvc
                .perform(
                        put("/api/location/" + location.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(locationUpdateDTO)))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("작성자가 아닌 유저가 장소 수정시")
    @Test
    public void 작성자가_아닌_유저가_장소_수정시() throws Exception {
        createOrLoadMember("testid", RoleStatus.NONE, "ROLE_USER");

        Member member = createOrLoadMember("notTestId", RoleStatus.CURATOR, "ROLE_USER");

        Location location = createOrLoadLocation(1, member);

        LocationUpdateDTO locationUpdateDTO = locationUpdateDTO();

        mockMvc
                .perform(
                        put("/api/location/" + location.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(objectMapper.writeValueAsString(locationUpdateDTO)))

                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("이벤트가 있는 장소 제거 시")
    @Test
    public void 이벤트가_있는_장소_제거시() throws Exception {
        Location location = createOrLoadLocation();
        createOrLoadEvent(location);

        mockMvc
                .perform(
                        delete("/api/location/" + location.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "noDelete", role = "USER")
    @DisplayName("작성자가 아닌 유저가 장소 삭제할 시")
    @Test
    public void 작성자가_아닌_유저가_장소를_삭제() throws Exception {
        createOrLoadMember("noDelete", RoleStatus.ARTIST_PENDING, "ROLE_USER");

        Location location = createOrLoadLocation();

        mockMvc
                .perform(
                        delete("/api/location/" + location.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @WithMockCustomUser(username = "testid", role = "ADMIN")
    @DisplayName("관리자가 장소 삭제를 시도할 시")
    @Test
    public void 관리자가_장소를_삭제() throws Exception {
        Location location = createOrLoadLocation();

        mockMvc
                .perform(
                        delete("/api/location/" + location.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockCustomUser(username = "testid", role = "USER")
    @DisplayName("작성자가 장소 삭제 테스트")
    @Test
    public void 작성자_장소_삭제_테스트() throws Exception {
        Member member = createOrLoadMember("testid", RoleStatus.CURATOR, "ROLE_USER");

        Location location = createOrLoadLocation(1, member);

        mockMvc
                .perform(
                        delete("/api/location/" + location.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
