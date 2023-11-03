package com.example.codebase.domain.member.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL)
    private Set<MemberAuthority> memberAuthorities;

    public static Authority of(String authorityName) {
        return Authority.builder()
                .authorityName(authorityName)
                .build();
    }
}
