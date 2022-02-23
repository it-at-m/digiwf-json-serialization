package io.muenchendigital.digiwf.json.serialization.configuration;

import io.muenchendigital.digiwf.json.serialization.SerializationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JsonSerializationAutoConfiguration {

    @Bean
    public SerializationService serializationService() {
        return new SerializationService();
    }

}