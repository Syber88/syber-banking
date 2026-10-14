package com.syber.banking.verification;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ponytail: writes the code to the application log in plaintext. DEVELOPMENT
// ONLY - in production this would hand every enrolment code to anyone with
// log access. Active unless banking.otp.channel=email.
public class LogOtpSender implements OtpSender {

    private static final Logger log = LoggerFactory.getLogger(LogOtpSender.class);

    @PostConstruct
    void warnLoudly() {
        log.warn("=".repeat(70));
        log.warn("OTP CODES ARE BEING LOGGED IN PLAINTEXT - DEVELOPMENT ONLY");
        log.warn("No email is sent. Set banking.otp.channel=email before deployment.");
        log.warn("=".repeat(70));
    }

    @Override
    public void send(String destination, String code) {
        log.warn("DEV OTP for {}: {}", destination, code);
    }
}
