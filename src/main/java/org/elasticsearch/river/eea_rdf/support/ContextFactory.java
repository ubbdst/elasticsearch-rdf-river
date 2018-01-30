package org.elasticsearch.river.eea_rdf.support;


/**
 * A Singleton for creating Context transformers
 *
 * @author Hemed Al Ruwehy
 */
public class ContextFactory {
    private static ContextTransformer transformer;

    private ContextFactory(){}

    public static ContextTransformer flatContext(String context) {
        if(transformer == null) {
            transformer = new FlatContextTransformer(context);
        }
        return transformer;
    }

}
