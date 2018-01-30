package org.elasticsearch.river.eea_rdf.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.util.IllegalFormatException;
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
    private ESLogger logger = Loggers.getLogger(getClass().getName());


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
        try {
            return parseJson(context).getAsJsonObject().getAsJsonObject(getContextKey());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("The context is malformed : " + context);
        }
    }


    /**
     * Parse JSON and returns JsonElement
     */
    public static JsonElement parseJson(String json) {
        return new JsonParser().parse(json);
    }

}
