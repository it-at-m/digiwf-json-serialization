/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik der Landeshauptstadt München, 2020
 */

package io.muenchendigital.digiwf.json.validation;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.regexp.RE2JRegexpFactory;
import org.json.JSONObject;

import java.util.Map;

/**
 * Json Schema Validator
 */
public class JsonSchemaValidator {

    /**
     * Validates data against a json schema
     *
     * @param schema schema that is used for validation
     * @param data   data that is validated
     */
    public void validate(final Map<String, Object> schema, final Map<String, Object> data) {
        this.validate(schema, new JSONObject(data));
    }

    /**
     * Validates data against a json schema
     *
     * @param schema schema that is used for validation
     * @param data   data that is validated
     */
    public void validate(final String schema, final Map<String, Object> data) {
        final Schema schemaObj = this.createSchema(new JSONObject(schema));
        schemaObj.validate(new JSONObject(data));
    }

    /**
     * Checks if a property is defined on the json schema.
     * Currently, not working for object lists and optional schemas
     *
     * @param schema      schema that is checked
     * @param jsonPointer path to property
     * @return if property is defined
     */
    public boolean definesProperty(final Map<String, Object> schema, final String jsonPointer) {
        final Schema schemaObj = this.createSchema(new JSONObject(schema));
        return this.definesProperty(schemaObj, jsonPointer);
    }

    /**
     * Checks if a property is defined on the json schema.
     * Currently not working for optional schemas
     *
     * @param schema      schema that is checked
     * @param jsonPointer path to property
     * @return if property is defined
     */
    public boolean definesProperty(final Schema schema, final String jsonPointer) {
        return schema.definesProperty(jsonPointer);
    }


    /**
     * Checks if a property is readonly on the json schema.
     * Currently, not working for object lists and optional schemas
     * Defines property should be checked first - otherwise returns false
     *
     * @param schema      schema that is checked
     * @param jsonPointer path to property
     * @return if property is defined
     */
    public boolean definesPropertyReadonly(final Map<String, Object> schema, final String jsonPointer) {
        final Schema schemaObj = this.createSchema(new JSONObject(schema));
        return this.definesPropertyReadonly(schemaObj, jsonPointer);
    }

    /**
     * Checks if a property is readonly on the json schema.
     * Currently, not working for object lists and optional schemas
     * Defines property should be checked first - otherwise returns false
     *
     * @param schema      schema that is checked
     * @param jsonPointer path to property
     * @return if property is defined
     */
    public boolean definesPropertyReadonly(final Schema schema, final String jsonPointer) {
        return schema.isReadOnlyProperty(jsonPointer);
    }

    //------------------------------------- helper methods -------------------------------------//

    private void validate(final Map<String, Object> schemaObject, final JSONObject data) {
        final Schema schema = this.createSchema(new JSONObject(schemaObject));
        schema.validate(data);
    }

    private Schema createSchema(final JSONObject schemaObject) {
        return SchemaLoader.builder().schemaJson(schemaObject)
                .draftV7Support()
                .regexpFactory(new RE2JRegexpFactory())
                .build()
                .load()
                .build();
    }
}
