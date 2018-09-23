package com.nikoskatsanos.dailysms;

/**
 * <p>SMS sender interface, for implementations that can send SMS</p>
 */
public interface SMSSender {

    void sendSMS(final String msg, final String recipient) throws SMSException;

    class SMSException extends Exception {
        public SMSException(String message) {
            super(message);
        }
    }

}
