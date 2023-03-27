package com.example.codebase.domain.member.entity;

import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.entity.oauth2.oAuthProvider;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "member")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, length = 50)
    private String username;

    @Column(name = "password", length = 100, nullable = true)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "picture")
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = true)
    private oAuthProvider oauthProvider;

    @Column(name = "oauth_provider_id", nullable = true)
    private String oauthProviderId;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "sns_url")
    private String snsUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "history")
    private String history;
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<MemberAuthority> authorities;

    public void setAuthorities(Set<MemberAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static User toUser(Member member) {
        return new User(member.getUsername(), member.getPassword(), member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority().getAuthorityName()))
                .collect(Collectors.toList()));
    }

    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public void setArtist(CreateArtistMemberDTO dto) {
        this.snsUrl = dto.getSnsUrl();
        this.websiteUrl = dto.getWebsiteUrl();
        this.introduction = dto.getIntroduction();
        this.history = dto.getHistory();
    }
}
