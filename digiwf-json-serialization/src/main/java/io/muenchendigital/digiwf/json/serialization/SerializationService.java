package io.muenchendigital.digiwf.json.serialization;


import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.regexp.RE2JRegexpFactory;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to handle data serialization and deserialization
 *
 */
public class SerializationService {

    public Map<String, Object> extractBySchema(final String schema, final Map<String, Object> data) {
        return this.deserializeData(schema, data);
    }

    public Map<String, Object> deserializeData(final String schema, final Map<String, Object> data) {
        final Schema schemaObject = this.createSchema(new JSONObject(schema));
        final List<String> schemaKeys = this.getKeys(schemaObject);
        final List<String> dataKeys = data.keySet().stream()
                .filter(schemaKeys::contains)
                .collect(Collectors.toList());

        final Map<String, Object> result = new HashMap<>();

        for (final String key : dataKeys) {
            if (data.containsKey(key)) {
                result.put(key, data.get(key));
            }
        }
        return result;
    }

    public Map<String, Object> serializeData(final String schemaObject, final Map<String, Object> rawData, final Map<String, Object> rawPreviousData) {
        return this.serializeData(schemaObject, new JSONObject(rawData), new JSONObject(rawPreviousData));
    }

    public Map<String, Object> serializeData(final Map<String, Object> schemaObject, final Map<String, Object> data, final Map<String, Object> previousData) {
        return this.serializeData(schemaObject, new JSONObject(data), new JSONObject(previousData));
    }

    //------------------------------------- serialization methods -------------------------------------//

    private Map<String, Object> serializeData(final String schemaObject, final JSONObject data, final JSONObject previousData) {
        final Schema schema = this.createSchema(new JSONObject(schemaObject));
        return this.serializeData(schema, data, previousData);
    }

    private Map<String, Object> serializeData(final Map<String, Object> schemaObject, final JSONObject data, final JSONObject previousData) {
        final Schema schema = this.createSchema(schemaObject);
        return this.serializeData(schema, data, previousData);
    }

    private Schema createSchema(final Map<String, Object> schemaObject) {
        final JSONObject rawSchema = new JSONObject(schemaObject);
        return this.createSchema(rawSchema);
    }

    private Schema createSchema(final JSONObject schema) {
        return SchemaLoader.builder().schemaJson(schema)
                .draftV7Support()
                .regexpFactory(new RE2JRegexpFactory())
                .build()
                .load()
                .build();
    }

    private Map<String, Object> serializeData(final Schema schema, final JSONObject data, final JSONObject previousData) {
        if (schema instanceof ObjectSchema) {
            return this.serializeObject(((ObjectSchema) schema).getPropertySchemas(), data, previousData);
        }

        //combined schemas are saved on the next higher object schema level -> search for the next object schema
        if (schema instanceof CombinedSchema) {
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return this.serializeData(combinedSchema.getSubschemas(), data, previousData);
        }

        return Collections.emptyMap();
    }

    private Map<String, Object> serializeObject(final Map<String, Schema> object, final JSONObject data, final JSONObject previousData) {
        final Map<String, Object> serializedData = new HashMap<>();


        //iterate through props and fill them with values
        for (final Map.Entry<String, Schema> entry : object.entrySet()) {

            //if readonly -> use the current value
            if (Optional.ofNullable(entry.getValue().isReadOnly()).orElse(false)) {

                //continue if previous value is no set
                if (!previousData.has(entry.getKey())) {
                    continue;
                }
                //override current value with readonly value
                serializedData.put(entry.getKey(), previousData.get(entry.getKey()));
                continue;
            }

            //if no value is passed null it
            if (!data.has(entry.getKey()) && previousData.has(entry.getKey())) {
                serializedData.put(entry.getKey(), null);
            }

            //if it is an nested object go recursive
            if (entry.getValue() instanceof ObjectSchema) {
                serializedData.put(entry.getKey(), new JSONObject(this.serializeData(entry.getValue(), data.getJSONObject(entry.getKey()), this.getOrEmpty(previousData, entry.getKey()))));
            } else if (entry.getValue() instanceof CombinedSchema && !entry.getValue().getUnprocessedProperties().containsKey("fieldType")) {
                serializedData.putAll(this.serializeData(entry.getValue(), data, previousData));
            } else {

                // if data is not in schema but already there use it -> could happen if a nested object is not fully represented in the schema
                previousData.toMap().keySet().forEach(dataKey -> {
                    if (!object.containsKey(dataKey)) {
                        serializedData.put(dataKey, previousData.get(dataKey));
                    }
                });

                //set data -> TODO use handlers for different schema types if necessary. Maybe something has to be changed if a date is passed or a user object
                if (data.has(entry.getKey()) || previousData.has(entry.getKey())) {
                    serializedData.putIfAbsent(entry.getKey(), data.has(entry.getKey()) ? data.get(entry.getKey()) : null);
                }
            }
        }

        return serializedData;
    }

    private Map<String, Object> serializeData(final Collection<Schema> schemas, final JSONObject data, final JSONObject previousData) {
        return schemas.stream()
                .map(schema -> this.serializeData(schema, data, previousData))
                .flatMap(m -> m.entrySet().stream())
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
    }

    private JSONObject getOrEmpty(final JSONObject data, final String key) {
        return data.has(key) ? data.getJSONObject(key) : new JSONObject();
    }

    //------------------------------------- extract keys methods -------------------------------------//

    private List<String> getKeys(final Schema schema) {
        if (schema instanceof ObjectSchema) {
            return new ArrayList<>(((ObjectSchema) schema).getPropertySchemas().keySet());
        }

        if (schema instanceof CombinedSchema) {
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return this.getKeys(combinedSchema.getSubschemas());
        }

        return Collections.emptyList();
    }

    private List<String> getKeys(final Collection<Schema> schemas) {
        return schemas.stream()
                .map(this::getKeys)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


}
