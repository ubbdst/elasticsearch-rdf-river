package org.elasticsearch.river.eea_rdf.support;


import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hemed Al Ruwehy
 * <p>
 * A calss for transforming Flat JSONLD Context. The value of a term definition in the context can either be
 * a simple string, mapping the term to an IRI, or a JSON object.
 */

public class FlatContextTransformer extends ContextTransformer {
    private final ESLogger logger = Loggers.getLogger(getClass().getName());
    private Map<String, String> contextProperties = new ConcurrentHashMap<>();
    private JsonElement context;

    @Inject
    FlatContextTransformer(String content) {
        this.context = extractContextElement(content);
    }

    public JsonElement getContextElement() {
        return context;
    }

    /**
     * Transform context to a flat map of string key-value pairs.
     *
     * @return a map where context values become keys and context keys become values.
     */
    @Override
    public Map<String, String> transform() {
        for (Map.Entry<String, JsonElement> entry : getContextElement().getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                String value = entry.getValue().getAsJsonObject().get(getContextId()).getAsString();
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
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) {
        String s = read("http://data.ub.uib.no/momayo/context.json", 20);
        Map<String, String> props = ContextFactory.flatContext(s).transform();
        System.out.println(new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(props)
        );

    }

}
