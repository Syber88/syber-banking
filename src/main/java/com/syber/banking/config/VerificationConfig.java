package com.syber.banking.config;

import com.syber.banking.verification.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VerificationConfig {

    @Bean
    @ConditionalOnProperty(name = "banking.verification.auto-approve", havingValue = "true")
    public IdentityVerifier autoIdentityVerifier() {
        return new AutoIdentityVerifier();
    }

    @Bean
    @ConditionalOnMissingBean(IdentityVerifier.class)
    public IdentityVerifier manualIdentityVerifier() {
        return new ManualIdentityVerifier();
    }
}