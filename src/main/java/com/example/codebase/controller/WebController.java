package com.example.codebase.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hizz")
public class WebController {

    @GetMapping()
    @PreAuthorize("permitAll()")
    public String goLogin() {
        return "swagger-ui";
    }

}
