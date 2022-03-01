package io.muenchendigital.digiwf.json.serialization.configuration;

import io.muenchendigital.digiwf.json.serialization.JsonSchemaSerializationService;
import io.muenchendigital.digiwf.json.serialization.serializer.JsonSchemaBaseSerializer;
import io.muenchendigital.digiwf.json.serialization.serializer.JsonSchemaSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JsonSerializationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JsonSchemaBaseSerializer jsonSchemaSerializer() {
        return new JsonSchemaSerializer();
    }

    @Bean
    public JsonSchemaSerializationService jsonSchemaSerializationService(final JsonSchemaBaseSerializer serializer) {
        return new JsonSchemaSerializationService(serializer);
    }

}