package com.example.codebase.jwt;

import antlr.Token;
import com.example.codebase.domain.auth.dto.LoginDTO;
import com.example.codebase.domain.auth.dto.TokenResponseDTO;
import com.example.codebase.domain.member.entity.Member;
import com.example.codebase.domain.member.repository.MemberRepository;
import com.example.codebase.exception.InvalidJwtTokenException;
import com.example.codebase.exception.NotFoundException;
import com.example.codebase.util.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider implements InitializingBean {
    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final Long tokenValidityInMilliseconds;
    private final Long refreshTokenValidityInMilliseconds;

    private final RedisUtil redisUtil;
    private Key key;

    public TokenProvider(
            AuthenticationManagerBuilder authenticationManagerBuilder, @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") Long tokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") Long refreshTokenValidityInMilliseconds, RedisUtil redisUtil,
            MemberRepository memberRepository) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
        this.redisUtil = redisUtil;
        this.memberRepository = memberRepository;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return createToken(authentication.getName(), authorities);
    }

    public String createToken(Member member) {
        String authorities = member.getAuthorities().stream()
                .map(a -> a.getAuthority().getAuthorityName())
                .collect(Collectors.joining(","));

        return createToken(member.getUsername(), authorities);
    }

    public String createToken(String sub, String authorities) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(sub)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }


    public String createRefreshToken(Authentication authentication) {
        return createRefreshToken(authentication.getName());
    }

    public String createRefreshToken(Member member) {
        return createRefreshToken(member.getUsername());
    }

    public String createRefreshToken(String sub) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(sub)
                .claim("typ", "refresh")
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }


    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);            // Token 값

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public TokenResponseDTO generateToken(LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = createToken(authentication);
        String refreshToken = createRefreshToken(authentication);

        // Redis에 Refresh Token 캐싱
        redisUtil.setDataAndExpire(authentication.getName() + "_token", refreshToken, refreshTokenValidityInMilliseconds);

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setAccessToken(accessToken);
        tokenResponseDTO.setExpiresIn(getTokenValidityInSeconds());
        tokenResponseDTO.setRefreshToken(refreshToken);
        tokenResponseDTO.setRefreshExpiresIn(getRefreshTokenValidityInSeconds());
        return tokenResponseDTO;
    }

    public TokenResponseDTO generateToken(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = createToken(authentication);
        String refreshToken = createRefreshToken(authentication);

        // Redis에 Refresh Token 캐싱
        redisUtil.setDataAndExpire(authentication.getName() + "_token", refreshToken, refreshTokenValidityInMilliseconds);

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setAccessToken(accessToken);
        tokenResponseDTO.setExpiresIn(getTokenValidityInSeconds());
        tokenResponseDTO.setRefreshToken(refreshToken);
        tokenResponseDTO.setRefreshExpiresIn(getRefreshTokenValidityInSeconds());
        return tokenResponseDTO;
    }

    public TokenResponseDTO regenerateToken(String token) {
        if (!validateToken(token)) {
            throw new InvalidJwtTokenException("유효하지 않은 토큰입니다");
        }

        String usernameFromToken = getClaims(token).getSubject();
        Optional<String> tokenData = redisUtil.getData(usernameFromToken + "_token");
        if (tokenData.isEmpty()) {
            throw new NotFoundException("서버에 저장되지 않은 토큰입니다.");
        }

        // Refresh Token 에서 User 아이디를 가져온다
        String savedRefreshToken = tokenData.get();
        if (!savedRefreshToken.equals(token)) {
            throw new InvalidJwtTokenException("요청에 담긴 토큰과 서버의 토큰이 일치하지 않습니다");
        }

        Member find = memberRepository.findByUsername(usernameFromToken).orElseThrow(
                () -> new NotFoundException("존재하지 않는 회원입니다")
        );

        String newAccessToken = createToken(find);
        String newRefreshToken = createRefreshToken(find);

        // Redis에 Refresh Token 캐싱
        redisUtil.setDataAndExpire(find.getUsername() + "_token", newRefreshToken, refreshTokenValidityInMilliseconds);

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO();
        tokenResponseDTO.setAccessToken(newAccessToken);
        tokenResponseDTO.setExpiresIn(getTokenValidityInSeconds());
        tokenResponseDTO.setRefreshToken(newRefreshToken);
        tokenResponseDTO.setRefreshExpiresIn(getRefreshTokenValidityInSeconds());
        return tokenResponseDTO;
    }


    private Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException exception) {
            log.info("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    public Long getTokenValidityInSeconds() {
        return tokenValidityInMilliseconds / 1000;
    }

    public Long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInMilliseconds / 1000;
    }

    public void removeRefreshToken(String username) {
        redisUtil.deleteData(username + "_token");
    }
}
