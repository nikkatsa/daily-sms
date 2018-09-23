package com.nikoskatsanos.dailysms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * <p>{@link SMSSender} implementation that uses <a href="https://www.twilio.com/">Twilio</a> to send SMS messages</p>
 */
@Service
@Profile({"twillio", "prod"})
public class TwilioSMSSender implements SMSSender {
    private static final Logger log = LogManager.getFormatterLogger(TwilioSMSSender.class);

    @Value("${twilio.SID}")
    private String twilioSID;

    @Value("${twilio.AuthToken}")
    private String twilioAuthToken;

    @Value("${twilio.PhoneNumber")
    private String twilioPhoneNumber;

    private volatile PhoneNumber fromTwilioPhoneNumber;

    @PostConstruct
    public void start() {
        log.info("Starting %s", this.getClass().getSimpleName());

        Objects.requireNonNull(this.twilioSID, "TwilioSID('-Dtwilio.SID') must be set");
        Objects.requireNonNull(this.twilioAuthToken, "TwilioAuthToken('-Dtwilio.AuthToken') must be set");
        Objects.requireNonNull(this.twilioPhoneNumber, "TwilioPhoneNumbe('-Dtwilio.PhoneNumber') must be set");

        Twilio.init(this.twilioSID, this.twilioAuthToken);
        this.fromTwilioPhoneNumber = new PhoneNumber(this.twilioPhoneNumber);

        log.info("Twilio service has been successfully initialized");
    }

    @Override
    public void sendSMS(final String msg, final String recipient) throws SMSException {
        log.info("Sending SMS=[msg=%s, to=%s]", msg, recipient);

        try {
            final Message twilioMessage = Message.creator(new PhoneNumber(recipient), this.fromTwilioPhoneNumber, msg).create();
            log.info("Twilio message sent %s", twilioMessage.toString());
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw new SMSException("Failed to send Twilio SMS message", e);
        }
    }
}
