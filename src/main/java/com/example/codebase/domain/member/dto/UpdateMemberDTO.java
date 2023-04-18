package com.example.codebase.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;
import javax.validation.constraints.Null;
import java.util.Optional;

@Getter
@Setter
public class UpdateMemberDTO {

    @Parameter(required = false)
    @Null
    private String username;

    @Parameter(required = false)
    @Null
    private String name;

    @Parameter(required = false)
    @Null
    private String email;

    @Parameter(required = false)
    @Null
    private String snsUrl;

    @Parameter(required = false)
    @Null
    private String websiteUrl;

    @Parameter(required = false)
    @Null
    private String introduction;

    @Parameter(required = false)
    @Null
    private String history;
}
