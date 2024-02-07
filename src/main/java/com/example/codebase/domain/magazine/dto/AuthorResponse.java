package com.example.codebase.domain.magazine.dto;

import com.example.codebase.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorResponse {

    private String authorUsername;

    private String authorName;

    private String authorProfileImage;

    private String authorIntroduction;

    private String authorCompanyName;

    private String authorCompanyRole;

    public static AuthorResponse from(Member member) {

        AuthorResponse response = new AuthorResponse();
        response.authorUsername = member.getUsername();
        response.authorName = member.getName();
        response.authorProfileImage = member.getPicture();
        response.authorIntroduction = member.getIntroduction();
        response.authorCompanyName = member.getCompanyName();
        response.authorCompanyRole = member.getCompanyRole();

        return response;
    }
}
