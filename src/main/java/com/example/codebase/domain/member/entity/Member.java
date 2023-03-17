package com.example.codebase.domain.member.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

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

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "activated")
    private boolean activated;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private Set<MemberAuthority> authorities;

    public void setAuthorities(Set<MemberAuthority> authorities) {
        this.authorities = authorities;
    }
}
