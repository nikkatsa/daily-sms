package com.nikoskatsanos.dailysms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * <p>Test only implementation of {@link SMSSender} which just print a message in the console</p>
 */
@Service
@Profile({"dev", "test"})
public class BlackholeSMSSender implements SMSSender {
    private static final Logger log = LogManager.getFormatterLogger(BlackholeSMSSender.class);

    @Override
    public void sendSMS(final String msg, final String recipient) throws SMSException {
        log.info("Sending SMS=[msg=%s, to=%s]", msg, recipient);
    }
}
