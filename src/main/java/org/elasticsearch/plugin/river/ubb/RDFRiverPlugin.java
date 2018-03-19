package org.elasticsearch.plugin.river.ubb;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.elasticsearch.river.ubb.RDFRiverModule;
import org.elasticsearch.river.ubb.settings.Settings;

/**
 * @author iulia, EEA
 */
public class RDFRiverPlugin extends AbstractPlugin {

    @Inject
    public RDFRiverPlugin() {
    }

    @Override
    public String name() {
        return Settings.RIVER_PLUGIN_NAME;
    }

    @Override
    public String description() {
        return "Turtle RDF River Plugin";
    }

    public void onModule(RiversModule module) {
        module.registerRiver(Settings.RIVER_SETTINGS_KEY, RDFRiverModule.class);
    }
}

