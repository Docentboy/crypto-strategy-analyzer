package ru.innopolis.attestations.attestation03.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.nio.charset.StandardCharsets;

@Configuration
public class WebConfig {

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        return converter;
    }
}