package com.example.codebase.domain.member.entity;

import com.example.codebase.domain.agora.entity.Agora;
import com.example.codebase.domain.artwork.entity.Artwork;
import com.example.codebase.domain.auth.OAuthAttributes;
import com.example.codebase.domain.follow.entity.Follow;
import com.example.codebase.domain.member.dto.CreateArtistMemberDTO;
import com.example.codebase.domain.member.dto.CreateCuratorMemberDTO;
import com.example.codebase.domain.member.dto.CreateMemberDTO;
import com.example.codebase.domain.member.dto.UpdateMemberDTO;
import com.example.codebase.domain.member.entity.oauth2.oAuthProvider;
import com.example.codebase.domain.notification.entity.NotificationReceivedStatus;
import com.example.codebase.domain.notification.entity.NotificationSetting;
import com.example.codebase.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Column(name = "company_role")
    private String companyRole;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "allow_email_receive")
    private boolean allowEmailReceive;

    @Column(name = "allow_email_receive_datetime")
    private LocalDateTime allowEmailReceiveDatetime;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Builder.Default
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime = LocalDateTime.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role_status")
    private RoleStatus roleStatus = RoleStatus.NONE;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<MemberAuthority> authorities = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Artwork> artworks = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Agora> agoras = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<NotificationReceivedStatus> notifications = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private NotificationSetting notificationSettings;

    @Builder.Default
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<Follow> followings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private List<Follow> followers = new ArrayList<>();

    public static User toUser(Member member) {
        return new User(member.getUsername(), member.getPassword(), member.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getAuthority().getAuthorityName()))
            .collect(Collectors.toList()));
    }

    public static Member from(PasswordEncoder passwordEncoder, OAuthAttributes oAuthAttributes) {
        String username = generateUniqueUsernameLikeYT();

        return Member.builder()
            .username(username)
            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
            .name(oAuthAttributes.getName())
            .email(oAuthAttributes.getEmail())
            .picture(oAuthAttributes.getPicture())
            .oauthProvider(oAuthAttributes.getRegistrationId())
            .oauthProviderId(oAuthAttributes.getOAuthProviderId())
            .createdTime(LocalDateTime.now())
            .updatedTime(LocalDateTime.now())
            .activated(true)
            .build();
    }

    private static String generateUniqueUsername() {
        // UUID 생성
        UUID uuid = UUID.randomUUID();

        // UUID를 문자열로 변환하고 "-"를 제거하여 username 생성
        String username = uuid.toString().replace("-", "");

        // "@"를 앞에 추가
        username = username.substring(0, 10); // 예시로 10자리만 사용
        return "user-" + username;
    }

    private static String generateUniqueUsernameLikeYT() {
        StringBuilder username = new StringBuilder("user-");

        // code :  영소문자1 + 영소문자2 + 숫자 = 26 * 26 * 9 = 6084
        // code1 + code2 + code3 + 영소문자 = 6084 * 6084 * 6084 * 26 = 5,855,189,618,304 경우의 수 (5조)
        for (int i = 0; i < 3; i++) {
            char alphabet1 = (char) ((Math.random() * 26) + 97);
            char alphabet2 = (char) ((Math.random() * 26) + 97);
            int number = (int) ((Math.random() * 9) + 1);

            username.append(alphabet1);
            username.append(alphabet2);
            username.append(number);
        }
        char last = (char) ((Math.random() * 26) + 97);
        username.append(last);

        return username.toString();
    }

    public static Member create(PasswordEncoder passwordEncoder, CreateMemberDTO member) {
        Member createMember = Member.builder()
                .username(member.getUsername())
                .password(passwordEncoder.encode(member.getPassword()))
                .name(member.getName())
                .email(member.getEmail())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .allowEmailReceive(member.getAllowEmailReceive())
                .activated(false)
                .build();

        createMember.allowEmailReceiveDatetime = member.getAllowEmailReceive() ? LocalDateTime.now() : null;
        return createMember;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addAuthority(MemberAuthority memberAuthority) {
        this.authorities.add(memberAuthority);
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(MemberAuthority.builder()
            .member(this)
            .authority(authority)
            .build());
    }

    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        this.updatedTime = LocalDateTime.now();
        return this;
    }

    public Member update(UpdateMemberDTO dto) {
        if (dto.getUsername() != null) {
            this.username = dto.getUsername();
        }
        if (dto.getName() != null) {
            this.name = dto.getName();
        }
        if (dto.getEmail() != null) {
            this.email = dto.getEmail();
        }
        if (dto.getSnsUrl() != null) {
            this.snsUrl = dto.getSnsUrl();
        }
        if (dto.getWebsiteUrl() != null) {
            this.websiteUrl = dto.getWebsiteUrl();
        }
        if (dto.getIntroduction() != null) {
            this.introduction = dto.getIntroduction();
        }
        if (dto.getHistory() != null) {
            this.history = dto.getHistory();
        }

        this.updatedTime = LocalDateTime.now();
        return this;
    }

    public Member update(String picture) {
        this.picture = picture;
        this.updatedTime = LocalDateTime.now();
        return this;
    }

    public void setArtist(CreateArtistMemberDTO dto) {
        this.snsUrl = dto.getSnsUrl();
        this.websiteUrl = dto.getWebsiteUrl();
        this.introduction = dto.getIntroduction();
        this.history = dto.getHistory();
        this.roleStatus = RoleStatus.ARTIST_PENDING;
        this.updatedTime = LocalDateTime.now();
    }

    public void updateRoleStatus(RoleStatus roleStatus) {
        this.roleStatus = roleStatus;
        this.updatedTime = LocalDateTime.now();
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
        this.updatedTime = LocalDateTime.now();
    }

    public void updateActivated(boolean activated) {
        this.activated = activated;
        this.updatedTime = LocalDateTime.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updatedTime = LocalDateTime.now();
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public void addArtwork(Artwork artwork) {
        this.artworks.add(artwork);
    }

    public void setCurator(CreateCuratorMemberDTO createCuratorMemberDTO) {
        this.snsUrl = createCuratorMemberDTO.getSnsUrl();
        this.websiteUrl = createCuratorMemberDTO.getWebsiteUrl();
        this.introduction = createCuratorMemberDTO.getIntroduction();
        this.history = createCuratorMemberDTO.getHistory();
        this.companyRole = createCuratorMemberDTO.getCompanyRole();
        this.companyName = createCuratorMemberDTO.getCompanyName();
        this.roleStatus = RoleStatus.CURATOR_PENDING;
        this.updatedTime = LocalDateTime.now();
    }

    public void addAgora(Agora agora) {
        this.agoras.add(agora);
    }

    public boolean equalsUsername(String username) {
        return this.username.equals(username);
    }

    public boolean isSubmitedRoleInformation() {
        return this.roleStatus == RoleStatus.ARTIST_PENDING || this.roleStatus == RoleStatus.CURATOR_PENDING || this.roleStatus == RoleStatus.ARTIST || this.roleStatus == RoleStatus.CURATOR;
    }

    public void updateEmailReceive(boolean emailReceive) {
        LocalDateTime updateTime = LocalDateTime.now();
        this.allowEmailReceive = emailReceive;
        this.allowEmailReceiveDatetime = updateTime;
        this.updatedTime = updateTime;
    }

    public void setNotificationSetting(NotificationSetting notificationSetting) {
        this.notificationSettings = notificationSetting;
    }
}
