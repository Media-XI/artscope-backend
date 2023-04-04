package com.example.codebase.domain.member.entity;

import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.entity.oauth2.oAuthProvider;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "member")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "member_id", columnDefinition = "BINARY(16)")
    @GeneratedValue(generator = "uuid4")
    @GenericGenerator(name = "UUID", strategy = "uuid4")
    private UUID id;

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

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "artist_status")
    private ArtistStatus artistStatus = ArtistStatus.NONE;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<MemberAuthority> authorities;

    public void setAuthorities(Set<MemberAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addAuthority(MemberAuthority memberAuthority) {
        this.authorities.add(memberAuthority);
    }

    public static User toUser(Member member) {
        return new User(member.getUsername(), member.getPassword(), member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority().getAuthorityName()))
                .collect(Collectors.toList()));
    }

    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        this.updatedTime = LocalDateTime.now();
        return this;
    }

    public void setArtist(CreateArtistMemberDTO dto) {
        this.picture = dto.getProfile();
        this.snsUrl = dto.getSnsUrl();
        this.websiteUrl = dto.getWebsiteUrl();
        this.introduction = dto.getIntroduction();
        this.history = dto.getHistory();
        this.artistStatus = ArtistStatus.PENDING;
        this.updatedTime = LocalDateTime.now();
    }
}
