package org.elasticsearch.river.ubb;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

/**
 * @author iulia
 */
public class RDFRiverModule extends AbstractModule {
    @Override
    public void configure() {
        bind(River.class).to(RDFRiver.class).asEagerSingleton();
    }
}
