package com.example.codebase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class ArtBackendApplication {

    @Value("${api.oauth2-redirect-uri}")
    private String oauth2RedirectUri;

    public static void main(String[] args) {
        SpringApplication.run(ArtBackendApplication.class, args);
    }

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.repair(); // flyway_schema_history 테이블에 오류가 있을 경우 repair()를 통해 오류를 수정한다.
            flyway.migrate(); // flyway.migrate()를 하면 flyway_schema_history 테이블에 마이그레이션 정보가 저장된다.
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info(oauth2RedirectUri);
        return new BCryptPasswordEncoder();
    }
}
