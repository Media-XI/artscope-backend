package com.example.codebase.controller;

import com.example.codebase.domain.event.dto.*;
import com.example.codebase.domain.event.service.EventService;
import com.example.codebase.domain.image.service.ImageService;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.exception.NotFoundMemberException;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.job.JobService;
import com.example.codebase.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "Event", description = "이벤트 API")
@RestController
@RequestMapping("/api/events")
@Validated
public class EventController {

    private final EventService eventService;

    private final MemberRepository memberRepository;

    private final ImageService imageService;

    private final JobService jobService;

    @Autowired
    public EventController(EventService eventService, ImageService imageService, MemberRepository memberRepository, JobService jobService) {
        this.eventService = eventService;
        this.memberRepository = memberRepository;
        this.imageService = imageService;
        this.jobService = jobService;
    }

    @Operation(summary = "이벤트 생성", description = "이벤트 일정을 생성합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity createEvent(
            @RequestPart(value = "dto") @Valid EventCreateDTO dto,
            @RequestPart(value = "mediaFiles") List<MultipartFile> mediaFiles,
            @RequestPart(value = "thumbnailFile") MultipartFile thumbnailFile)
            throws Exception {
        String username =
                SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        dto.validateDates();

        Member member = memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        if (!member.isSubmitedRoleInformation()) {
            throw new RuntimeException("추가정보 입력한 사용자만 이벤트를 생성할 수 있습니다.");
        }

        imageService.uploadMedias(dto, mediaFiles);
        imageService.uploadThumbnail(dto.getThumbnail(), thumbnailFile);

        EventDetailResponseDTO event = eventService.createEvent(dto, member);

        return new ResponseEntity(event, HttpStatus.CREATED);
    }

    @Operation(summary = "이벤트 목록 조회", description = "전체 이벤트 목록을 조회 합니다.")
    @GetMapping()
    public ResponseEntity getEvent(
            @ModelAttribute @Valid EventSearchDTO eventSearchDTO,
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {
        eventSearchDTO.repeatTimeValidity();

        EventsResponseDTO dtos = eventService.getEvents(eventSearchDTO, page, size, sortDirection);
        return new ResponseEntity(dtos, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 상세 조회", description = "이벤트 상세를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity getEventDetail(@PathVariable Long eventId) {
        EventDetailResponseDTO eventDetailResponseDTO = eventService.getEventDetail(eventId);
        return new ResponseEntity(eventDetailResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 수정", description = "이벤트를 수정합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/{eventId}")
    public ResponseEntity updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid EventUpdateDTO dto){
        String username =
                SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        EventDetailResponseDTO eventDetailResponseDTO = eventService.updateEvent(eventId, dto, username);

        return new ResponseEntity(eventDetailResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "이벤트 삭제", description = "이벤트를 삭제합니다.")
    @PreAuthorize("isAuthenticated() and hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity deleteEvent(@PathVariable Long eventId) {
        String username =
                SecurityUtil.getCurrentUsername().orElseThrow(() -> new RuntimeException("로그인이 필요합니다."));

        eventService.deleteEvent(eventId, username);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "수동 이벤트 크롤링 업데이트", description = "수동으로 공공데이터 포털에서 이벤트를 가져옵니다")
    @PreAuthorize("isAuthenticated() AND hasRole('ROLE_ADMIN')")
    @PostMapping("/crawling/event")
    public ResponseEntity crawlingEvent(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date) {
        String currentDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        try {
            jobService.getEventList(currentDate);
        } catch (IOException e) {
            return new ResponseEntity("이벤트 크롤링에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("이벤트가 업데이트 되었습니다.", HttpStatus.OK);
    }

    @Operation(summary = "이벤트 작성자를 통해 이벤트 목록 조회", description = "이벤트 작성자를 통해 이벤트 목록을 조회합니다.")
    @GetMapping("/members/{username}")
    public ResponseEntity getEventByMember(
            @PathVariable String username,
            @PositiveOrZero @RequestParam(value = "page", defaultValue = "0") int page,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC", required = false) String sortDirection) {

        Member member = memberRepository.findByUsername(username).orElseThrow(NotFoundMemberException::new);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdTime");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        EventsResponseDTO dtos = eventService.getEventsByMember(member,pageRequest);
        return new ResponseEntity(dtos, HttpStatus.OK);
    }

}
