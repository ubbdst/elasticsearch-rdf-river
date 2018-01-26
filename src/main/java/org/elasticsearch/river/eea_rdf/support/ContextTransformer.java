package org.elasticsearch.river.eea_rdf.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Map;


/**
 * @author  Hemed Al Ruwehy
 *
 * Transformer for JSONLD context
 */
abstract class ContextTransformer extends JsonFileLoader {

    private static final String CONTEXT_KEY = "@context";
    private static final String CONTEXT_ID = "@id";


    public static String getContextId() {
        return CONTEXT_ID;
    }


    public static String getContextKey() {
        return CONTEXT_KEY;
    }


    /**
     * Transforms JSONLD context to Map of key-value strings. This is easily portable
     *
     * @return a map of sting key-values
     */
    public abstract Map<String, String> transform();


    /**
     * Extract JSONLD context key as a JsonElement
     */
    public JsonElement extractContextElement(String context) {
        JsonElement con = new JsonParser().parse(context);
        if (con.isJsonObject() && con.getAsJsonObject().getAsJsonObject(getContextKey()) != null) {
            return con.getAsJsonObject().getAsJsonObject(getContextKey());
        }
        throw new IllegalArgumentException("JSON context must have @context key");
    }


}
