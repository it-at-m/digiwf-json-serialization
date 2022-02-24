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
public class JsonSchemaSerializer implements JsonSchemaBaseSerializer {

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
        final List<String> schemaKeys = this.getKeys(schema);
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
     * Helper to serialize data for object schemas.
     *
     * @param object
     * @param data
     * @param previousData
     * @return
     */
    private Map<String, Object> serializeObject(final Map<String, Schema> object, final JSONObject data, final JSONObject previousData) {
        final Map<String, Object> serializedData = new HashMap<>();

        // iterate through props and fill them with values
        for (final Map.Entry<String, Schema> entry : object.entrySet()) {

            // if readonly -> use the current value
            if (Optional.ofNullable(entry.getValue().isReadOnly()).orElse(false)) {

                // continue if previous value is not set
                if (!previousData.has(entry.getKey())) {
                    continue;
                }
                // override current value with readonly value
                serializedData.put(entry.getKey(), previousData.get(entry.getKey()));
                continue;
            }

            // if no value is passed null it
            if (!data.has(entry.getKey()) && previousData.has(entry.getKey())) {
                serializedData.put(entry.getKey(), null);
            }

            // if it is a nested object go recursive
            if (entry.getValue() instanceof ObjectSchema) {
                final JSONObject prevData = previousData.has(entry.getKey()) ? previousData.getJSONObject(entry.getKey()) : new JSONObject();
                serializedData.put(entry.getKey(), new JSONObject(this.serialize(entry.getValue(), data.getJSONObject(entry.getKey()), prevData)));
            } else if (entry.getValue() instanceof CombinedSchema && !entry.getValue().getUnprocessedProperties().containsKey("fieldType")) {
                serializedData.putAll(this.serialize(entry.getValue(), data, previousData));
            } else {

                // if data is not in schema but already there use it -> could happen if a nested object is not fully represented in the schema
                previousData.toMap().keySet().forEach(dataKey -> {
                    if (!object.containsKey(dataKey)) {
                        serializedData.put(dataKey, previousData.get(dataKey));
                    }
                });

                // set data -> TODO use handlers for different schema types if necessary. Maybe something has to be changed if a date is passed or a user object
                if (data.has(entry.getKey()) || previousData.has(entry.getKey())) {
                    serializedData.putIfAbsent(entry.getKey(), data.has(entry.getKey()) ? data.get(entry.getKey()) : null);
                }
            }
        }

        return serializedData;
    }

    /**
     * Helper method that returns all keys that are in the json schema.
     *
     * @param schema
     * @return
     */
    private List<String> getKeys(final Schema schema) {
        if (schema instanceof ObjectSchema) {
            return new ArrayList<>(((ObjectSchema) schema).getPropertySchemas().keySet());
        }

        // if schema is a combined schema iterate all sub schemas and get the keys for every sub schema
        // by calling getKeys(...) recursively
        if (schema instanceof CombinedSchema) {
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return combinedSchema.getSubschemas().stream()
                    .map(this::getKeys)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
