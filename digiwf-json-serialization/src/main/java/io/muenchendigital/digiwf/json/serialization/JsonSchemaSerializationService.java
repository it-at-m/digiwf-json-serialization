package io.muenchendigital.digiwf.json.serialization;

import io.muenchendigital.digiwf.json.serialization.serializer.JsonSchemaSerializer;
import lombok.RequiredArgsConstructor;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.regexp.RE2JRegexpFactory;
import org.json.JSONObject;

import java.util.Map;

/**
 * Service to handle data serialization and deserialization
 */
@RequiredArgsConstructor
public class JsonSchemaSerializationService {

    private final JsonSchemaSerializer serializer;

    /**
     * Serialize data for a specific schema
     * <p>
     * Pass the rawData that you want to serialize and the rawPreviousData.
     * According to the rawData the rawPreviousData is updated and returned.
     *
     * @param schemaObject
     * @param rawData
     * @param rawPreviousData
     * @return serialized data
     */
    public Map<String, Object> serializeData(final String schemaObject, final Map<String, Object> rawData, final Map<String, Object> rawPreviousData) {
        final Schema schema = JsonSchemaSerializationService.createSchema(new JSONObject(schemaObject));
        return this.serializer.serialize(schema, new JSONObject(rawData), new JSONObject(rawPreviousData));
    }

    /**
     * Serialize data for a specific schema
     * <p>
     * Pass the rawData that you want to serialize and the rawPreviousData.
     * According to the rawData the rawPreviousData is updated and returned.
     *
     * @param schemaObject
     * @param rawData
     * @param rawPreviousData
     * @return serialized data
     */
    public Map<String, Object> serializeData(final Map<String, Object> schemaObject, final Map<String, Object> rawData, final Map<String, Object> rawPreviousData) {
        final Schema schema = JsonSchemaSerializationService.createSchema(new JSONObject(schemaObject));
        return this.serializer.serialize(schema, new JSONObject(rawData), new JSONObject(rawPreviousData));
    }

    /**
     * @param schemaObject
     * @param rawData
     * @param rawPreviousData
     * @return serialized data
     */
    public Map<String, Object> getAccessMap(final String schemaObject, final Map<String, Object> rawData, final Map<String, Object> rawPreviousData) {
        final Schema schema = JsonSchemaSerializationService.createSchema(new JSONObject(schemaObject));
        return this.serializer.serialize(schema, new JSONObject(rawData), new JSONObject(rawPreviousData));
    }

    /**
     * Deserialize data for a specific schema
     *
     * @param schema
     * @param data
     * @return deserialized data
     */
    public Map<String, Object> deserializeData(final String schema, final Map<String, Object> data) {
        final Schema schemaObject = JsonSchemaSerializationService.createSchema(new JSONObject(schema));
        return this.serializer.deserialize(schemaObject, data);
    }

    /**
     * Create and load schema for json schema version draft 7
     *
     * @param schema
     * @return
     */
    public static Schema createSchema(final JSONObject schema) {
        return SchemaLoader.builder().schemaJson(schema)
                .draftV7Support()
                .regexpFactory(new RE2JRegexpFactory())
                .build()
                .load()
                .build();
    }

    /**
     * Create and load schema for json schema version draft 7
     *
     * @param schema
     * @return
     */
    public static Schema createSchema(final String schema) {
        return SchemaLoader.builder().schemaJson(new JSONObject(schema))
                .draftV7Support()
                .regexpFactory(new RE2JRegexpFactory())
                .build()
                .load()
                .build();
    }
}
