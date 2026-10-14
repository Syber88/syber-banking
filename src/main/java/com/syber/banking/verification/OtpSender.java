package com.syber.banking.verification;

/** Delivers an enrolment code. The destination's meaning depends on the channel. */
public interface OtpSender {
    void send(String destination, String code);
}
