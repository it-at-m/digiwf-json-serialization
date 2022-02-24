package io.muenchendigital.digiwf.json.serialization;


import io.muenchendigital.digiwf.json.serialization.serializer.JsonSchemaSerializer;
import org.assertj.core.api.Assertions;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonSchemaSerializationServiceTest {

    private JsonSchemaSerializationService jsonSchemaSerializationService;

    @BeforeEach
    private void setUp() {
        this.jsonSchemaSerializationService = new JsonSchemaSerializationService(new JsonSchemaSerializer());
    }

    @Test
    public void serializeSimpleData() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/serialization/simpleSchema.json");

        final Map<String, Object> data = Map.of(
                "stringProp1", "stringValue",
                "numberProp1", 12
        );

        final Map<String, Object> previousData = Map.of();

        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, previousData);

        Assertions.assertThat(serializedData).isEqualTo(Map.of(
                "stringProp1", "stringValue"
        ));
    }

    @Test
    public void serializeData() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/serialization/schema.json");

        final Map<String, Object> data = Map.of(
                "stringProp1", "fsdafsda"
        );

        final Map<String, Object> previousData = Map.of(
                "dateprop", "20"
        );

        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, previousData);
        final Map<String, Object> validData = new HashMap<>();

        validData.put("stringProp1", "fsdafsda");
        validData.put("dateprop", null);

        //override all
        Assertions.assertThat(serializedData).isEqualTo(validData);
    }


    @Test
    public void serializeDataAndUpdateWithReadonlyValues() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/serialization/simpleSchema.json");

        final Map<String, Object> data = Map.of(
                "stringProp1", "stringValue",
                "numberProp1", 12
        );

        final Map<String, Object> previousData = Map.of(
                "numberProp1", 100,
                "stringProp2", "100"
        );

        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, previousData);

        final Map<String, Object> validData = new HashMap<>();

        validData.put("stringProp1", "stringValue");
        validData.put("numberProp1", 100);
        validData.put("stringProp2", null);

        //override all
        Assertions.assertThat(serializedData).isEqualTo(validData);

        Assertions.assertThat(serializedData).isEqualTo(validData);
    }

    @Test
    public void serializeCombinedSchemaData() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/serialization/schema.json");

        final Map<String, Object> data = Map.of(
                "textarea1", "textAreaValue",
                "booleanprop", true,
                "dateprop", "2020-10-1",
                "stringProp1", "stringValue",
                "numberProp1", 12
        );

        final Map<String, Object> previousData = Map.of(
                "numberProp1", 100
        );

        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, previousData);

        Assertions.assertThat(serializedData).isEqualTo(Map.of(
                "textarea1", "textAreaValue",
                "booleanprop", true,
                "dateprop", "2020-10-1",
                "stringProp1", "stringValue",
                "numberProp1", 100
        ));
    }

    @Test
    public void serializeCombinedObjectSchemaData() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/serialization/objectSchema.json");

        final Map<String, Object> data = Map.of(
                "textarea1", "textAreaValue",
                "booleanprop", true,
                "dateprop", "2020-10-1",
                "stringProp1", "stringValue",
                "numberProp1", 12,
                "objectProp", Map.of(
                        "stringProp1", "test"
                )
        );

        final Map<String, Object> previousData = Map.of(
                "numberProp1", 100
        );

        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, previousData);

        final Map<String, Object> erg = Map.of(
                "textarea1", "textAreaValue",
                "booleanprop", true,
                "dateprop", "2020-10-1",
                "stringProp1", "stringValue",
                "numberProp1", 100,
                "objectProp", Map.of(
                        "stringProp1", "test"
                ));

        Assertions.assertThat(new JSONObject(serializedData).toString()).isEqualTo(new JSONObject(erg).toString());
    }


    @Test
    public void serializeCustomTypes() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/serialization/customTypesSchema.json");

        final Map<String, Object> data = Map.of(
                "FormField_Grusstext", "meinValue"
        );

        final Map<String, Object> previousData = Map.of();

        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, previousData);

        Assertions.assertThat(serializedData).isEqualTo(Map.of(
                "FormField_Grusstext", "meinValue"
        ));
    }

    @Test
    public void serializeComplexObjectStructure() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "textarea", "100",
                "textfeld", "100",
                "objekt1", Map.of(
                        "objektTextfeld", "fdsfsdafsdafadsfsadfsdafd",
                        "objektSchalter", true)

        );

        final String rawSchema = this.getSchemaString("/schema/validation/complexObjectSchema.json");
        final Map<String, Object> serializedData = this.jsonSchemaSerializationService.serializeData(rawSchema, data, Map.of());

        final Map<String, Object> erg = Map.of(
                "textarea", "100",
                "textfeld", "100",
                "objekt1", new JSONObject(Map.of(
                        "objektTextfeld", "fdsfsdafsdafadsfsadfsdafd",
                        "objektSchalter", true)));

        Assertions.assertThat(this.areEqual(erg, serializedData)).isEqualTo(true);
    }

    //------------------------------------ Helper Methods ------------------------------------//

    private String getSchemaString(final String path) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(path).toURI())));
    }

    private boolean areEqual(final Map<String, Object> first, final Map<String, Object> second) {
        if (first.size() != second.size()) {
            return false;
        }

        return first.entrySet().stream()
                .allMatch(e -> this.compareObject(e.getValue(), second.get(e.getKey())));
    }

    private boolean compareObject(final Object obj1, final Object obj2) {
        if (obj1 instanceof JSONObject) {
            return obj1.toString().equals(obj2.toString());
        }

        return obj1.equals(obj2);
    }
}
