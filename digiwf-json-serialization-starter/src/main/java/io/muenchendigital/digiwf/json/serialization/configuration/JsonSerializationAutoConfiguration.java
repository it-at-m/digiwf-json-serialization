package io.muenchendigital.digiwf.json.serialization.configuration;

import io.muenchendigital.digiwf.json.serialization.JsonSchemaSerializationService;
import io.muenchendigital.digiwf.json.serialization.serializer.JsonSchemaSerializer;
import io.muenchendigital.digiwf.json.serialization.serializer.JsonSchemaSerializerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JsonSerializationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JsonSchemaSerializer jsonSchemaSerializer() {
        return new JsonSchemaSerializerImpl();
    }

    @Bean
    public JsonSchemaSerializationService jsonSchemaSerializationService(final JsonSchemaSerializer serializer) {
        return new JsonSchemaSerializationService(serializer);
    }

}