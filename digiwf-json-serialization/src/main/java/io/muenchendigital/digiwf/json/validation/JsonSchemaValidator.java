/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik der Landeshauptstadt München, 2020
 */

package io.muenchendigital.digiwf.json.validation;

import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ObjectSchema;
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
     *
     * @param schema   schema that is checked
     * @param property property that is search for
     * @return if property is defined
     */
    public boolean definesProperty(final Map<String, Object> schema, final String property) {
        final Schema schemaObj = this.createSchema(new JSONObject(schema));
        return this.definesProperty(schemaObj, property);
    }

    /**
     * Checks if a property is defined on the json schema.
     *
     * @param schema   schema that is checked
     * @param property property that is search for
     * @return if property is defined
     */
    public boolean definesProperty(final Schema schema, final String property) {
        if (schema instanceof ObjectSchema) {
            return schema.definesProperty(property);
        }

        if (schema instanceof CombinedSchema) {
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return combinedSchema.getSubschemas().stream().anyMatch(obj -> this.definesProperty(obj, property));
        }

        return false;
    }

    /**
     * @param schema   Schema to search for the property
     * @param property property
     * @return
     */
    public boolean isReadOnlyProperty(final Schema schema, final String property) {
        if (schema instanceof ObjectSchema) {
            return this.isReadOnlyProperty((ObjectSchema) schema, property);
        }

        if (schema instanceof CombinedSchema) {
            //TODO not possible to set CombinedSchemas readOnly right now
            final CombinedSchema combinedSchema = (CombinedSchema) schema;
            return combinedSchema.getSubschemas().stream().anyMatch(obj -> this.isReadOnlyProperty(obj, property));
        }

        //TODO Conditional Schema

        return false;
    }

    //------------------------------------- helper methods -------------------------------------//

    private boolean isReadOnlyProperty(final ObjectSchema schema, String field) {
        field = field.replaceFirst("^#", "").replaceFirst("^/", "");
        final int firstSlashIdx = field.indexOf('/');
        final String nextToken;
        final String remaining;
        if (firstSlashIdx == -1) {
            nextToken = field;
            remaining = null;
        } else {
            nextToken = field.substring(0, firstSlashIdx);
            remaining = field.substring(firstSlashIdx + 1);
        }
        return !field.isEmpty() && this.isReadOnlyProperty(schema, nextToken, remaining);
    }

    private boolean isReadOnlyProperty(final ObjectSchema schema, String current, final String remaining) {
        current = this.unescape(current);
        final boolean hasSuffix = !(remaining == null);
        if (schema.getPropertySchemas().containsKey(current)) {
            if (schema.isReadOnly() != null && schema.isReadOnly()) {
                return true;
            } else if (hasSuffix) {
                return this.isReadOnlyProperty(schema.getPropertySchemas().get(current), remaining);
            } else if (schema.getPropertySchemas().get(current).isReadOnly() != null && schema.getPropertySchemas().get(current).isReadOnly()) {
                return true;
            }
        }
        return schema.isReadOnly() != null && schema.isReadOnly();
    }

    private String unescape(final String token) {
        return token.replace("~1", "/").replace("~0", "~")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

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
