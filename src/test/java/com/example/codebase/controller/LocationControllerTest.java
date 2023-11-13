package com.example.codebase.controller;

import com.example.codebase.domain.auth.WithMockCustomUser;
import com.example.codebase.domain.location.dto.LocationCreateDTO;
import com.example.codebase.domain.location.entity.Location;
import com.example.codebase.domain.location.repository.LocationRepository;
import com.example.codebase.domain.member.entity.Authority;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.entity.MemberAuthority;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    public Member createOrLoadMember() {
        return createOrLoadMember("testid", "ROLE_ADMIN");
    }

    public Member createOrLoadMember(String username, String... authorities) {
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
                .build();

        for (String authority : authorities) {
            MemberAuthority memberAuthority = new MemberAuthority();
            memberAuthority.setAuthority(Authority.of(authority));
            memberAuthority.setMember(dummy);
            dummy.addAuthority(memberAuthority);
        }

        return memberRepository.save(dummy);
    }

    public Location createOrLoadLocation() {
        return createOrLoadLocation(1);
    }

    public Location createOrLoadLocation(int index) {
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
                .build();

        return locationRepository.save(location);
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

    @WithMockCustomUser(username = "testid", role = "ADMIN")
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
}
