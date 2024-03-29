package com.example.codebase.domain.member.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", columnDefinition = "BINARY(16)")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "authority_name")
    private Authority authority;

    public void setMember(Member member) {
        this.member = member;
        member.addAuthority(this);
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
        authority.addMemberAuthority(this);
    }
}
