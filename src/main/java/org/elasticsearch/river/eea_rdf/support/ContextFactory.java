package org.elasticsearch.river.eea_rdf.support;


/**
 * A Singleton for creating Context transformers
 *
 * @author Hemed Al Ruwehy
 */
public class ContextFactory {
    private static ContextTransformer transformer;

    private ContextFactory() {
    }


    /**
     * Creates Flat Context implementation
     */
    public static synchronized ContextTransformer flatContext() {
        if (transformer == null) {
            transformer = new FlatContextTransformer();
        }
        return transformer;
    }

    /**
     * Create Flat context implementation for a given context
     */
    public static ContextTransformer flatContext(String context) {
        if (transformer == null) {
            transformer = new FlatContextTransformer(context);
        }
        return transformer;
    }

}
