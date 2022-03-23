package io.muenchendigital.digiwf.json.serialization.configuration;

import io.muenchendigital.digiwf.json.serialization.JsonSerializationService;
import io.muenchendigital.digiwf.json.serialization.serializer.JsonSerializer;
import io.muenchendigital.digiwf.json.serialization.serializer.JsonSerializerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JsonSerializationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JsonSerializer jsonSchemaSerializer() {
        return new JsonSerializerImpl();
    }

    @Bean
    public JsonSerializationService jsonSchemaSerializationService(final JsonSerializer serializer) {
        return new JsonSerializationService(serializer);
    }

}