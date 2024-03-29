package com.example.codebase.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Artscope API 명세서")
                .description("Artscope API 명세서")
                .version("v1");

        String jwtTokenScheme = "JWT 토큰값";

        // API 요청 Header에 JWT 토큰값을 넣어야 함을 명시
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtTokenScheme);

        // Security Schemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtTokenScheme, bearerAuthSecurityScheme(jwtTokenScheme));

        // Server
        Server server = new Server();
        server.setUrl("/");
        server.setDescription("기본 서버");

        Server server2 = new Server();
        server2.setUrl("https://api.seonghoon.xyz");

        Server server3 = new Server();
        server3.setUrl("https://api.artscope.kr");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server, server2, server3))
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private SecurityScheme bearerAuthSecurityScheme(String jwtTokenScheme) {
        return new SecurityScheme()
                .name(jwtTokenScheme)
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");
    }
}
