package org.elasticsearch.river.eea_rdf.support;

import com.google.gson.JsonElement;

import java.util.Map;


/**
 * @author Hemed Al Ruwehy
 * University of Bergen Library
 * <p>
 * <p>
 * Transformer for JSONLD context. Simply speaking, a context is used to map terms to IRIs.
 * A context in JSON-LD allows two applications to use shortcut terms to communicate with one another more efficiently,
 * but without losing accuracy.
 * Terms are case sensitive and any valid string that is not a reserved JSON-LD keyword can be used as a term.
 * <p>
 * For more about context, see : https://www.w3.org/TR/json-ld/#the-context
 */
public interface ContextTransformer {

    String CONTEXT_KEY = "@context";
    String CONTEXT_ID = "@id";


    /**
     * Transforms JSONLD context to Map of key-value strings. Leaving implementation to sub-classes
     *
     * @return a map of sting key-values
     */
    Map<String, String> transform(String context);


    /**
     * Extract JSONLD context key as a JsonElement
     */
    default JsonElement extractContextElement(String context) {
        try {
            return JsonFileLoader.parseJson(context).getAsJsonObject().getAsJsonObject(CONTEXT_KEY);
        } catch (Exception e) {
            throw new IllegalArgumentException("Context is malformed. Expected a valid JSON string with \"@context\"" +
                    " as its parent key but found [" + context + "]");
        }
    }

}
