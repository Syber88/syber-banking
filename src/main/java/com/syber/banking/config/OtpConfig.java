package com.syber.banking.config;

import com.syber.banking.verification.EmailOtpSender;
import com.syber.banking.verification.LogOtpSender;
import com.syber.banking.verification.OtpSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Note the default here is the LOG sender, the opposite of VerificationConfig,
 * where absent configuration must mean strict. Falling back to the log stops
 * working locally and says so loudly; silently falling back to real email on a
 * missing credential would be worse.
 */
@Configuration
public class OtpConfig {

    @Bean
    @ConditionalOnProperty(name = "banking.otp.channel", havingValue = "email")
    public OtpSender emailOtpSender(JavaMailSender mailSender,
                                    @Value("${banking.otp.from}") String from) {
        return new EmailOtpSender(mailSender, from);
    }

    @Bean
    @ConditionalOnMissingBean(OtpSender.class)
    public OtpSender logOtpSender() {
        return new LogOtpSender();
    }
}
