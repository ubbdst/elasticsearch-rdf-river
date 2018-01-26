package org.elasticsearch.river.eea_rdf.support;


import com.google.gson.JsonElement;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hemed Al Ruwehy
 * <p>
 * A calss for transforming Plain JSONLD Context.
 */

public class PlainContextTransformer extends ContextTransformer {
    private final ESLogger logger = Loggers.getLogger(getClass().getName());
    private Map<String, String> contextProperties = new HashMap<>();
    private JsonElement context;

    PlainContextTransformer(String context) {
        this.context = extractContextElement(context);
    }

    public JsonElement getContextElement() {
        return context;
    }

    /**
     * Convert context to Map of String values
     */
    @Override
    public Map<String, String> transform() {
        for (Map.Entry<String, JsonElement> entry : getContextElement().getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                String value = entry.getValue().getAsJsonObject().get(getContextId()).getAsString();
                contextProperties.put(value, entry.getKey());
            } else if (entry.getValue().isJsonArray()) {
                logger.warn("Expected URI but found JSON array in the context for property [{}]." +
                        "This property will be ignored", entry.getKey());
            } else if (entry.getValue().isJsonNull()) {
                logger.warn("Expected URI but found JSON Null in the context for property [{}]. " +
                        "This property will be ignored", entry.getKey());
            } else if (entry.getValue().isJsonPrimitive()) {
                logger.warn("Expected URI but found primitive data type in the context for property [{}]. " +
                        "This property will be ignored", entry.getKey());
            } else {
                contextProperties.put(entry.getValue().toString(), entry.getKey());
            }
        }
        return contextProperties;
    }


}
