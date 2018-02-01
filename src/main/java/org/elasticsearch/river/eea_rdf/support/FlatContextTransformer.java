package org.elasticsearch.river.eea_rdf.support;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.river.eea_rdf.utils.FileManager;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hemed Al Ruwehy
 * <p>
 * A calss for transforming Flat JSONLD Context. The value of a term definition in the context can either be
 * a simple string, mapping the term to an IRI, or a JSON object.
 */

public class FlatContextTransformer implements ContextTransformer  {

    private final ESLogger logger = Loggers.getLogger(getClass().getName());
    private Map<String, String> contextProperties = new ConcurrentHashMap<>();
    private JsonElement context;


    FlatContextTransformer(String content) {
        this.context = extractContextElement(content);
    }

    FlatContextTransformer() { }

    /**
     * Get a given context
     */
    public JsonElement getContextElement() {
        return context;
    }


    /**
     * Transform context to a flat map of string key-value pairs.
     *@param context a context JSON string
     * @return a map where context values become keys and context keys become values.
     */
    @Override
    public Map<String, String> transform(String context) {
        Objects.requireNonNull(context, "Context cannot be null");
        this.context = extractContextElement(context);
        return transform(this.context);
    }


    /**
     * Transform context to a flat map of string key-value pairs.
     */
    public Map<String, String> transform() {
        Objects.requireNonNull(context, "Context cannot be null");
        return transform(context);
    }


    /**
     * Transform context to a flat map of string key-value pairs.
     *
     *@param context a context JsonElement
     * @return a map where context values become keys and context keys become values.
     */
    public Map<String, String> transform(JsonElement context) {
        Objects.requireNonNull(context, "Context cannot be null");
        for (Map.Entry<String, JsonElement> entry : context.getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                String value = entry.getValue().getAsJsonObject().get(CONTEXT_ID).getAsString();
                contextProperties.put(value, entry.getKey());
            } else if (entry.getValue().isJsonArray()) {
                logger.warn("Expected IRI but found JSON array for property [{}]." +
                        "This property will be ignored", entry.getKey());
            } else if (entry.getValue().isJsonNull()) {
                logger.warn("Expected IRI but found JSON Null for property [{}]. " +
                        "This property will be ignored", entry.getKey());
            } else if (entry.getValue().isJsonPrimitive()) {
                logger.warn("Expected IRI but found primitive data type for property [{}]. " +
                        "This property will be ignored", entry.getKey());
            } else {
                contextProperties.put(entry.getValue().toString(), entry.getKey());
            }
        }
        return contextProperties;
    }


    /**
     * Main method for easy debugging
     */
    public static void main(String[] args) {
        String s = FileManager.read("http://data.ub.uib.no/momayo/context.json", 20);
        Map<String, String> props = ContextFactory.flatContext().transform(s);
        System.out.println(new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(props)
        );

    }

}
