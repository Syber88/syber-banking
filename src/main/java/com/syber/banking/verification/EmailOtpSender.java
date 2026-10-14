package com.syber.banking.verification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * The message deliberately carries no name, account or ID details: if it
 * reaches the wrong inbox it leaks nothing but the fact that a code was issued.
 */
public class EmailOtpSender implements OtpSender {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailOtpSender(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String destination, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(destination);
        message.setSubject("Your Syber Banking enrolment code");
        message.setText("""
                Your enrolment code is %s.
                It expires in 5 minutes.

                If you did not request this, you can ignore this email.
                """.formatted(code));

        mailSender.send(message);
    }
}
