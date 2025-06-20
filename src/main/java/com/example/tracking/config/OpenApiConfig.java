package com.example.tracking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tracking Number Generator API")
                        .version("1.0")
                        .description("API for generating scalable tracking numbers.")
                        .contact(new Contact()
                                .name("Prakash Raj")
                                .email("prakashkaraj@gmail.com")));
    }
}
