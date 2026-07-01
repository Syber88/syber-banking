package com.syber.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankingAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Syber-Banking API")
                        .description("REST API for the Digital Syber-Banking System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Syber")
                                .email("siyamgz88@email.com")));
    }
}