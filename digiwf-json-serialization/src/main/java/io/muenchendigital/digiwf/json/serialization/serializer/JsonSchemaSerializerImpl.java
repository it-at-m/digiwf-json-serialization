package io.muenchendigital.digiwf.json.serialization.serializer;

import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JsonSchemaSerializer is an implementation of the JsonSchemaBaseSerializer that provides functionality
 * to serialize and deserialize data based on a json schema.
 */
public class JsonSchemaSerializerImpl implements JsonSchemaSerializer {

    /**
     * Serialize data based on the schema.
     * <p>
     * Returns the data for the jsonSchema.
     * If an object is readonly the value from the previousData is applied. Otherwise, the serializer uses the value from data.
     *
     * @param schema
     * @param data
     * @param previousData
     * @return
     */
    @Override
    public Map<String, Object> serialize(final Schema schema, final JSONObject data, final JSONObject previousData) {
        if (schema instanceof ObjectSchema) {
            return this.serializeObject(((ObjectSchema) schema).getPropertySchemas(), data, previousData);
        }

        // combined schemas are saved on the next higher object schema level -> search for the next object schema
        // therefore iterate all sub schemas of the schema and serialize the data for every schema
        // by calling serialize(...) recursively
        if (schema instanceof CombinedSchema) {
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return combinedSchema.getSubschemas().stream()
                    .map(subSchema -> this.serialize(subSchema, data, previousData))
                    .flatMap(m -> m.entrySet().stream())
                    .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
        }

        return Collections.emptyMap();
    }

    /**
     * Deserialize data based on the schema.
     * <p>
     * This function compares the keys from data with the keys provided in the json schema
     * and returns all keys from the json schema with the values found in the data map.
     *
     * @param schema
     * @param data
     * @return deserialized data
     */
    @Override
    public Map<String, Object> deserialize(final Schema schema, final Map<String, Object> data) {

        // remove all keys from data that are not in schema
        final List<String> schemaKeys = this.extractRootKeys(schema);
        final List<String> dataKeys = data.keySet().stream()
                .filter(schemaKeys::contains)
                .collect(Collectors.toList());

        final Map<String, Object> result = new HashMap<>();

        // add available data to the results
        for (final String key : dataKeys) {
            if (data.containsKey(key)) {
                result.put(key, data.get(key));
            }
        }
        return result;
    }

    /**
     * Returns all root keys that are in the json schema.
     *
     * @param schema
     * @return root keys
     */
    public List<String> extractRootKeys(final Schema schema) {
        if (schema instanceof ObjectSchema) {
            return new ArrayList<>(((ObjectSchema) schema).getPropertySchemas().keySet());
        }

        // if schema is a combined schema iterate all sub schemas and get the keys for every sub schema
        // by calling getKeys(...) recursively
        if (schema instanceof CombinedSchema) {
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return combinedSchema.getSubschemas().stream()
                    .map(this::extractRootKeys)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    //--------------------------------------------------- helper methods ---------------------------------------------------//


    private Map<String, Object> serializeObject(final Map<String, Schema> object, final JSONObject data, final JSONObject previousData) {

        // iterate through props and fill them with values
        for (final Map.Entry<String, Schema> entry : object.entrySet()) {

            // if it is a nested object go recursive
            //TODO Handle ArrayObject Lists
            if (entry.getValue() instanceof ObjectSchema) {
                previousData.put(entry.getKey(), this.serializeObjectSchema(data, previousData, entry));
            } else if (entry.getValue() instanceof CombinedSchema && !entry.getValue().getUnprocessedProperties().containsKey("fieldType")) {
                this.serialize(entry.getValue(), data, previousData).forEach(previousData::put);
            } else {

                if (Boolean.TRUE != entry.getValue().isReadOnly()) {
                    previousData.put(entry.getKey(), data.has(entry.getKey()) ? data.get(entry.getKey()) : null);
                }
            }
        }
        return previousData.toMap();
    }

    private JSONObject serializeObjectSchema(final JSONObject data, final JSONObject previousData, final Map.Entry<String, Schema> entry) {
        final JSONObject prevData = previousData.has(entry.getKey()) ? previousData.getJSONObject(entry.getKey()) : new JSONObject();
        final Map<String, Object> objectData = this.serializeObject(((ObjectSchema) entry.getValue()).getPropertySchemas(), data, prevData);
        return new JSONObject(objectData);
    }

}
