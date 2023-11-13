package com.example.codebase.util;


import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.domain.member.dto.AuthorityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SecurityUtil {

    public SecurityUtil() {
    }

    public static Optional<String> getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || isAnonymous()) {
            log.debug("Security Context에 인증 정보 없습니다.");
            return Optional.empty();
        }

        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }

    public static boolean isAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보 없습니다.");
            return true;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    public static Optional<Set<AuthorityDto>> getCurrentUserRoles() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.debug("Security Context에 인증 정보 없습니다.");
            return Optional.empty();
        }

        Set<AuthorityDto> authorityDtos = null;
        authorityDtos = authentication.getAuthorities().stream()
            .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthority()).build())
            .collect(Collectors.toSet());

        return Optional.ofNullable(authorityDtos);
    }

    public static Boolean isAdmin() {
        Set<AuthorityDto> currentUserRoles = SecurityUtil.getCurrentUserRoles().get();
        return currentUserRoles.stream().anyMatch(authorityDto -> authorityDto.getAuthorityName().equals("ROLE_ADMIN"));
    }

    public static Boolean isSameUser(String username1, String username2) {
        return username1.equals(username2);
    }

    /*
        Username을 가진 사람이 관리자 이거나, 현재 스프링 컨텍스트에 저장된 유저와 같은 사람인가(같은 스레드 요청인지)
     */
    public static Boolean isAdminOrSameUser(String username) {
        return isAdmin() || isSameUser(username, getCurrentUsername().get());
    }

    public static String getCookieAccessTokenValue(TokenResponseDTO tokenDto) {
        // TODO : Domain 설정
        String sb = "access-token=" + tokenDto.getAccessToken() +
            "; Path=/; Max-Age=" +
            tokenDto.getExpiresIn() +
            "; HttpOnly; Secure";
        return sb;
    }

}