package com.nikoskatsanos.dailysms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;

/**
 * <p>Interface for different SMS message sources. A source can be anything:
 * <ul>
 * <li>Plain test</li>
 * <li>A web service returning any type of information</li>
 * </ul>
 * </p>
 */
public interface SMSSource {

    /**
     * @return Some text representing the output of this {@link SMSSource}
     */
    String getText();

    /**
     * <p>A {@link SMSSource} that returns a prefix, greeting etc</p>
     */
    @Component
    @Order(value = 1)
    public class PrefixedSMSSource implements SMSSource {

        @Value("${dailySMS.SMSPrefix:Hello,}")
        private String prefix;

        @Override
        public String getText() {
            return Optional.ofNullable(this.prefix).orElse("");
        }
    }

    /**
     * <p>A {@link SMSSource} returning the day of the week</p>
     */
    @Order(value = 2)
    @Component
    public class DayOfWeekSMSSource implements SMSSource {


        @Override
        public String getText() {
            return String.format("It's %s", LocalDate.now().getDayOfWeek().toString());
        }
    }

    /**
     * <p>A {@link SMSSource} that returns free text</p>
     */
    @Component
    @Order(value = 4)
    public class FreeTextSMSSource implements SMSSource {

        @Value("${dailySMS.SMSFreeText}")
        private String text;

        @Override
        public String getText() {
            return Optional.ofNullable(this.text).orElse("");
        }
    }

    /**
     * <p>A {@link SMSSource} that connects to web service at <a href="https://geek-jokes.sameerkumar.website/api">https://geek-jokes.sameerkumar.website/api</a> and returns a geeky joke</p>
     */
    @Component
    @Order(value = 3)
    public class GeekJokeSMSSource implements SMSSource {
        private static final String GEEK_JOKE_URL = "https://geek-jokes.sameerkumar.website/api";
        private static final String NO_GEEK_JOKE_TODAY = "No geek joke for today :(";

        @Autowired
        private RestTemplate restTemplate;

        private String retrieveGeekJoke() {
            final ResponseEntity<String> httpResponse = this.restTemplate.getForEntity(GEEK_JOKE_URL, String.class);
            switch (httpResponse.getStatusCode()) {
                case OK:
                    return httpResponse.getBody().replaceAll("\"", "");
                default:
                    return NO_GEEK_JOKE_TODAY;
            }
        }

        @Override
        public String getText() {
            return this.retrieveGeekJoke();
        }
    }
}
