package com.example.codebase.domain.artwork.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkLikeMemberId implements Serializable {

    private UUID member;

    private Long artwork;
}
