package com.nikoskatsanos.dailysms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class DailySMSAppRunner {
    private static final Logger log = LogManager.getFormatterLogger(DailySMSAppRunner.class);

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    public static void main(final String... args) {
        log.info("Starting %s", DailySMSAppRunner.class.getSimpleName());

        SpringApplication.run(DailySMSAppRunner.class);
    }
}
