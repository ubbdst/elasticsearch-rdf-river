package org.elasticsearch.river.eea_rdf.support;
import java.util.Map;


/**
 * Transformeer for JSONLD context
 */
abstract class ContextTransformer extends JsonFileLoader {

    /**
     * Transforms JSONLD context to Map of key-value strings. This is easily portable
     *
     * @return a map of sting key-values
     */
    public abstract Map<String, String> transform();
}
