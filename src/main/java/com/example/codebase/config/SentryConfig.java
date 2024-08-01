package com.example.codebase.config;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class SentryConfig {

    private final String SENTRY_DSN;

    private final Environment env;

    @Autowired
    public SentryConfig(
            @Value("${sentry.dsn}") String SENTRY_DSN,
            Environment env
    ) {
        this.SENTRY_DSN = SENTRY_DSN;
        this.env = env;
    }

    @Bean
    @Profile({"dev", "prod"})
    public String sentryInit() {
        String environment = env.getProperty("spring.profiles.active");
        Sentry.init(options -> {
            options.setDsn(SENTRY_DSN);
            options.setEnvironment(environment);
            options.setTracesSampleRate(1.0);
            options.setEnableUncaughtExceptionHandler(true);
        });
        log.info("Sentry initialized. ${} environment", environment);
        return "Sentry initialized.";
    }
}
