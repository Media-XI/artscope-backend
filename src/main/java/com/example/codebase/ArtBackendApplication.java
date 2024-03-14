package com.example.codebase;

import com.example.codebase.infra.DeployAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class ArtBackendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ArtBackendApplication.class, args);

        if (context.containsBean("deployAlertService")) {
            context.getBean(DeployAlertService.class).deployAlert();
        }
    }

    @Bean
    @Profile({"dev", "prod"})
    public DeployAlertService deployAlertService() {
        return new DeployAlertService();
    }
}
