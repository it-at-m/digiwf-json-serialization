package io.muenchendigital.digiwf.json.serialization.serializer;

import org.everit.json.schema.Schema;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * JsonSchemaBaseSerializer is an interface which is used in the JsonSchemaSerializationService to serialize and deserialize data based on a json schema.
 */
public interface JsonSchemaSerializer {

    /**
     * Serialize data according to the provided json schema and the previous data.
     *
     * @param schema
     * @param data
     * @param previousData
     * @return
     */
    Map<String, Object> serialize(final Schema schema, final JSONObject data, final JSONObject previousData);


    /**
     * Deserialize the data according to the provided json schema.
     *
     * @param schema
     * @param data
     * @return
     */
    Map<String, Object> deserialize(final Schema schema, final Map<String, Object> data);


    /**
     * Returns all root keys that are in the json schema.
     *
     * @param schema
     * @return
     */
    List<String> extractRootKeys(final Schema schema);

}
