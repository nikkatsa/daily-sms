package com.nikoskatsanos.dailysms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>SMS composer is a facade between {@link SMSSource}s which produce messages and a {@link SMSSender} which sends those messages</p>
 */
@Service
public class SMSComposer {
    private static final Logger log = LogManager.getFormatterLogger(SMSComposer.class);

    private final SMSSender smsSender;
    private final List<SMSSource> smsSources;

    @Value("${dailySMS.Recipients}")
    private String[] recipients;

    @Value("${dailySMS.ScheduleCRONExpression}")
    private String scheduleCRONExpression;

    public SMSComposer(@Autowired SMSSender smsSender, @Autowired List<SMSSource> smsSources) {
        this.smsSender = smsSender;
        this.smsSources = smsSources;
    }

    @PostConstruct
    public void start() {
        log.info("Starting %s with CRON expression=[%s]", this.getClass().getSimpleName(), this.scheduleCRONExpression);
    }

    @Scheduled(cron = "${dailySMS.ScheduleCRONExpression}")
    public void scheduleSMS() {
        if (Objects.nonNull(this.recipients)) {
            Stream.of(this.recipients).forEach(r -> {
                try {
                    final String msg = String.join(". ", this.smsSources.stream().map(SMSSource::getText).collect(Collectors.toList()));
                    this.smsSender.sendSMS(msg, r);
                } catch (final SMSSender.SMSException e) {
                    log.error("Error occurred while sending SMS to recipient=%s", r, e);
                } catch (final Exception e) {
                    log.error("Unexpected error occurred while scheduling SMS for recipient=%s", r, e);
                }
            });
        }
    }
}
