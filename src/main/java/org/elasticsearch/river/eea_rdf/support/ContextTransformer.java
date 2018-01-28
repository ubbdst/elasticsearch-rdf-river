package org.elasticsearch.river.eea_rdf.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Map;


/**
 * @author Hemed Al Ruwehy
 * <p>
 * Transformer for JSONLD context. Simply speaking, a context is used to map terms to IRIs.
 * A context in JSON-LD allows two applications to use shortcut terms to communicate with one another more efficiently,
 * but without losing accuracy.
 * Terms are case sensitive and any valid string that is not a reserved JSON-LD keyword can be used as a term.
 * <p>
 * For more about context, see : https://www.w3.org/TR/json-ld/#the-context
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
     * Transforms JSONLD context to Map of key-value strings. Leaving implementation to sub-classes
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
