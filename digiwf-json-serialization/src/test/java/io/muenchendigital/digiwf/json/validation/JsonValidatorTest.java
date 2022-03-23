package io.muenchendigital.digiwf.json.validation;

import io.muenchendigital.digiwf.json.factory.JsonSchemaFactory;
import io.muenchendigital.digiwf.json.serialization.JsonSerializationService;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static io.muenchendigital.digiwf.json.serialization.JsonSerializationService.createSchema;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonValidatorTest {

    private JsonSchemaValidator validationService;

    @BeforeEach
    private void setUp() {
        this.validationService = new JsonSchemaValidator();
    }

    @Test
    public void simpleCheckRequired() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "numberProp1", 12,
                "stringProp1", "fdsfsdafsdafadsfsadfsdafd"
        );
        final String rawSchema = this.getSchemaString("/schema/validation/simpleSchema.json");
        this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
    }

    @Test
    public void simpleCheckRegex() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "numberProp1", 12,
                "stringProp1", "fdsfsdafsdafadsfsadfsdafdfdsfsdafsdafadsfsadfsdafd"
        );
        final String rawSchema = this.getSchemaString("/schema/validation/simpleSchema.json");
        final ValidationException exception = assertThrows(ValidationException.class, () -> {
            this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
        });

        assertThat(exception.getMessage()).isEqualTo("#/stringProp1: string [fdsfsdafsdafadsfsadfsdafdfdsfsdafsdafadsfsadfsdafd] does not match pattern ^.{1,30}$");
    }

    @Test
    public void checkRequired() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "stringProp1", "fds",
                "textarea1", "fdsa",
                "numberProp1", 12,
                "booleanprop", false,
                "dateprop", "2020-10-10"
        );
        final String rawSchema = this.getSchemaString("/schema/validation/schema.json");

        this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
    }

    @Test
    public void checkRequiredFailed() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "stringProp1", "fdsfsdafsdafadsfsadfsdafd"
        );
        final String rawSchema = this.getSchemaString("/schema/validation/simpleSchema.json");
        final ValidationException exception = assertThrows(ValidationException.class, () -> {
            this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
        });

        assertThat(exception.getMessage()).isEqualTo("#: required key [numberProp1] not found");
    }

    @Test
    public void checkObjectStructure() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "numberProp1", 100,
                "objectProp1", Map.of(
                        "stringProp1", "fdsfsdafsdafadsfsadfsdafd",
                        "numberProp1", 12
                )
        );

        final String rawSchema = this.getSchemaString("/schema/validation/objectSchema.json");
        this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
    }

    @Test
    public void validateComplexObjectStructure() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "textarea", "100",
                "textfeld", "100",
                "objekt1", Map.of(
                        "objektTextfeld", "fdsfsdafsdafadsfsadfsdafd",
                        "objektSchalter", true
                )
        );

        final String rawSchema = this.getSchemaString("/schema/validation/complexObjectSchema.json");
        this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
    }


    @Test
    public void validateListObjectSchema() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "ibanEinzahler", "MyEinzahler2",
                "betragInCent", 5,
                "FormField_Empfaenger", List.of("260"),
                "nameEinzahler", "MyEinzahler"
        );

        final String rawSchema = this.getSchemaString("/schema/validation/listObjectSchema.json");
        this.validationService.validate(new JSONObject(rawSchema).toMap(), data);
    }

    @Test
    public void textAreaCheck() throws URISyntaxException, IOException {
        final Map<String, Object> data = Map.of(
                "numberProp1", 12,
                "GrundDienstlNotwendigkeit", "fdsfsdafsdafadsfsadfsdafd"
        );
        final String rawSchema = this.getSchemaString("/schema/validation/textAreaSchema.json");

        this.validationService.validate(JsonValidatorTest.getSchemaMap(rawSchema), data);
    }

    @Test
    public void checkDefinesPropertyInObjectSchema() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/validation/complexObjectReadonlyValidationSchemaMultiple.json");

        final Schema schema = createSchema(rawSchema);

        // 1. #/fahrzeugdaten/fahrzeuge
        // 2. $.fahrzeugdaten.fahrzeuge.dokumente
        // 2. $.fahrzeugdaten.personendaten.dokumente
        // -> #/fahrzeugdaten/fahrzeuge/fahrzeugschein


        //1. allof-1.allOf-1.personendaten.fahrzeuge (frontend-feld)
        //2. Gib mir bitte alle meine Dokumente

        // #/personendaten/fahrzeuge
        /*
            {
             "personendaten" : {
                "dokumente" : "s3-pfad"
             }


         */

        final boolean b = new JsonSchemaValidator().definesProperty(schema, "#/fahrzeugdaten/fahrzeuge");
        assertTrue(b);
    }

    @Test
    public void checkDefinesPropertyInConditionalSchema() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/validation/conditionalSubSchema.json");

        final Schema schema = createSchema(rawSchema);

        final boolean b = new JsonSchemaValidator().definesProperty(schema, "#/stringProp2");
        assertTrue(b);
    }

    @Test
    @Disabled
    public void checkDefinesPropertyInIfElseSchema() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/validation/ifElseSchema.json");

        final Schema schema = createSchema(rawSchema);
        final boolean b = new JsonSchemaValidator().definesProperty(schema, "#/stringProp3");

        assertTrue(b);
    }

    @Test
    public void checkIsReadOnlyPropertyInObjectSchema() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/validation/complexObjectReadonlyValidationSchemaMultiple.json");

        final Schema schema = createSchema(rawSchema);
        final boolean b = new JsonSchemaValidator().definesPropertyReadonly(schema, "#/fahrzeugdaten/fahrzeuge");

        assertTrue(b);
    }

    @Test
    public void checkDefinesPropertyComplexConditionalSchema() throws URISyntaxException, IOException {
        final String rawSchema = this.getSchemaString("/schema/validation/complexConditionalSchema.json");

        final Schema schema = createSchema(rawSchema);

        final boolean b = new JsonSchemaValidator().definesProperty(schema, "#/antragsdaten/datumAntragstellung/stringProp1");
        assertTrue(b);
    }


    @Test
    public void checkObjectWithAdditionalPropertiesFalse() throws IOException, URISyntaxException {

        final String rawSchema = this.getSchemaString("/schema/validation/complexObjectSchemaAdditionalPropertiesFalse.json");
        final Schema schema = JsonSerializationService.createSchema(rawSchema);
        final Map<String, Object> data = Map.of(
                "textarea", "12",
                "objekt1", Map.of("objektTextfeld", "abc")
        );
        schema.validate(new JSONObject(data));
    }


    //------------------------------------ Helper Methods ------------------------------------//


    public static Map<String, Object> getSchemaMap(final String schemaString) {
        return JsonSchemaFactory.gson().fromJson(schemaString, JsonSchemaFactory.mapType());
    }

    private String getSchemaString(final String path) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(path).toURI())));
    }


}
