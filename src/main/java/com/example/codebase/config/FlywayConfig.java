package com.example.codebase.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.repair(); // flyway_schema_history 테이블에 오류가 있을 경우 repair()를 통해 오류를 수정한다.
            flyway.migrate(); // flyway.migrate()를 하면 flyway_schema_history 테이블에 마이그레이션 정보가 저장된다.
        };
    }

}
