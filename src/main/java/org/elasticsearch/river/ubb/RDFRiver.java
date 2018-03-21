package org.elasticsearch.river.ubb;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.river.*;
import org.elasticsearch.river.ubb.settings.Defaults;
import org.elasticsearch.river.ubb.support.ContextFactory;
import org.elasticsearch.river.ubb.support.Harvester;
import org.elasticsearch.river.ubb.support.JsonFileLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author European Environment Agency (EEA)
 * @author Hemed Al Ruwehy
 * <p>
 * Modified by Hemed Ali Al Ruwehy, The University of Bergen Library
 * @since 09-03-2015
 */
public class RDFRiver extends AbstractRiverComponent implements River {
    private static final ESLogger logger = Loggers.getLogger(RDFRiver.class);
    private volatile Harvester harvester;
    private volatile Thread harvesterThread;

    @Inject
    public RDFRiver(RiverName riverName,
                    RiverSettings settings,
                    @RiverIndexName String riverIndexName,
                    Client client) {
        super(riverName, settings);
        harvester = new Harvester();
        harvester.client(client).riverName(riverName.name());
        buildHarvester(settings);
    }

    /**
     * Type casting accessors for river settings
     **/
    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractSettings(RiverSettings settings, String key) {
        return (Map<String, Object>) settings.settings().get(key);
    }

    /**
     * Type casting accessors for river settings
     **/
    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractSettings(RiverSettings settings) {
        if (settings.settings().containsKey(Defaults.EEA_SETTINGS_KEY)) {
            return (Map<String, Object>) settings.settings().get(Defaults.EEA_SETTINGS_KEY);
        }
        throw new IllegalArgumentException(String.
                format("No key in the river settings. Expected \"%s\"", Defaults.EEA_SETTINGS_KEY));
    }


    @SuppressWarnings("unchecked")
    private static Map<String, String> getStrStrMapFromSettings(Map<String, Object> settings, String key) {
        return (Map<String, String>) settings.get(key);
    }


    @SuppressWarnings("unchecked")
    private static Map<String, String> loadProperties(Map<String, Object> settings, String key) {
        Object values = settings.get(key);
        if (values instanceof Map) {
            return getStrStrMapFromSettings(settings, key);
        }
        return new JsonFileLoader().resolveToFlatMap(values.toString());
    }

    private static String loadContext(Map<String, Object> settings, String key) {
        Object values = settings.get(key);
        logger.info("Reading from context: " + values);
        return new JsonFileLoader().resolveToString(values.toString());
    }


    @SuppressWarnings("unchecked")
    private static Map<String, Object> getStrObjMapFromSettings(Map<String, Object> settings, String key) {
        return (Map<String, Object>) settings.get(key);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getStrListFromSettings(Map<String, Object> settings, String key) {
        return (List<String>) settings.get(key);
    }

    /**
     * Builds harvester
     */
    private void buildHarvester(RiverSettings settings) {
        Map<String, Object> rdfSettings = extractSettings(settings);
        harvester.rdfIndexType(XContentMapValues.nodeStringValue(
                rdfSettings.get("indexType"), "full"))
                .rdfStartTime(XContentMapValues.nodeStringValue(
                        rdfSettings.get("startTime"), ""))
                .rdfUrl(XContentMapValues.nodeStringValue(
                        rdfSettings.get("uris"), "[]"))
                .rdfEndpoint(XContentMapValues.nodeStringValue(
                        rdfSettings.get("endpoint"), ""))
                .rdfTDBLocation(XContentMapValues.nodeStringValue(
                        rdfSettings.get("tdbLocation"), ""))
                .rdfQueryPath(XContentMapValues.nodeStringValue(
                        rdfSettings.get("queryPath"), ""))
                .rdfNumberOfBulkActions(XContentMapValues.nodeLongValue(
                        rdfSettings.get("bulkActions"),
                        Defaults.DEFAULT_NUMBER_OF_BULK_ACTIONS))
                .rdfUpdateDocuments(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("updateDocuments"),
                        Defaults.DEFAULT_UPDATE_DOCUMENTS))
                .rdfQueryType(XContentMapValues.nodeStringValue(
                        rdfSettings.get("queryType"),
                        Defaults.DEFAULT_QUERYTYPE))
                .rdfListType(XContentMapValues.nodeStringValue(
                        rdfSettings.get("listtype"),
                        Defaults.DEFAULT_LIST_TYPE))
                .rdfAddLanguage(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("addLanguage"),
                        Defaults.DEFAULT_ADD_LANGUAGE))
                .rdfLanguage(XContentMapValues.nodeStringValue(
                        rdfSettings.get("language"),
                        Defaults.DEFAULT_LANGUAGE))
                .rdfAddUriForResource(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("includeResourceURI"),
                        Defaults.DEFAULT_ADD_URI))
                .removeIllegalCharsForSuggestion(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("removeIllegalCharsForSuggestion"),
                        true))
                .deleteRiverAfterCreation(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("deleteRiverAfterCreation"), false))
                .generateSortLabel(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("generateSortLabel"), false))
                .maxSuggestInputLength(XContentMapValues.nodeIntegerValue(
                        rdfSettings.get("maxSuggestInputLength"),
                        Defaults.DEFAULT_MAX_SUGGEST_INPUT_LENGTH))
                /*.rdfURIDescription(XContentMapValues.nodeStringValue(
                       rdfSettings.get("uriDescription"),
                       EEASettings.DEFAULT_URI_DESCRIPTION))
                */
                .rdfSyncConditions(XContentMapValues.nodeStringValue(
                        rdfSettings.get("syncConditions"),
                        Defaults.DEFAULT_SYNC_COND))
                /*.rdfContextProp(XContentMapValues.nodeStringValue(
                        rdfSettings.get("context"), ""))
                        */
                .rdfSyncTimeProp(XContentMapValues.nodeStringValue(
                        rdfSettings.get("syncTimeProp"),
                        Defaults.DEFAULT_SYNC_TIME_PROP))
                .rdfSyncOldData(XContentMapValues.nodeBooleanValue(
                        rdfSettings.get("syncOldData"),
                        Defaults.DEFAULT_SYNC_OLD_DATA));

        if (rdfSettings.containsKey("uriDescription")) {
            harvester.rdfURIDescription(getStrListFromSettings(rdfSettings, "uriDescription"));
        } else {
            //Convert the default array to List
            List<String> defaultUriList = Arrays.asList(Defaults.DEFAULT_URI_DESCRIPTION);
            harvester.rdfURIDescription(defaultUriList);
        }
        if (rdfSettings.containsKey("proplist")) {
            harvester.rdfPropList(getStrListFromSettings(rdfSettings, "proplist"));
        }
        if (rdfSettings.containsKey("query")) {
            harvester.rdfQuery(getStrListFromSettings(rdfSettings, "query"));
        } else {
            harvester.rdfQuery(Defaults.DEFAULT_QUERIES);
        }
        /*if (rdfSettings.containsKey("normProp")) {
            harvester.rdfNormalizationProp(getStrStrMapFromSettings(rdfSettings, "normProp"));
        }*/
        if (rdfSettings.containsKey("context")) {
            harvester.rdfContextProp(loadContext(rdfSettings, "context"), ContextFactory.flatContext());
        }
        if (rdfSettings.containsKey("normProp")) {
            harvester.rdfNormalizationProp(loadProperties(rdfSettings, "normProp"));
        }
        if (rdfSettings.containsKey("normMissing")) {
            harvester.rdfNormalizationMissing(getStrStrMapFromSettings(rdfSettings, "normMissing"));
        }
        if (rdfSettings.containsKey("normObj")) {
            harvester.rdfNormalizationObj(getStrStrMapFromSettings(rdfSettings, "normObj"));
        }
        if (rdfSettings.containsKey("blackMap")) {
            harvester.rdfBlackMap(getStrObjMapFromSettings(rdfSettings, "blackMap"));
        }
        if (rdfSettings.containsKey("whiteMap")) {
            harvester.rdfWhiteMap(getStrObjMapFromSettings(rdfSettings, "whiteMap"));
        }
        if (settings.settings().containsKey("index")) {
            Map<String, Object> indexSettings = extractSettings(settings, "index");
            harvester.index(XContentMapValues.nodeStringValue(indexSettings.get("index"),
                    Defaults.DEFAULT_INDEX_NAME))
                    .type(XContentMapValues.nodeStringValue(indexSettings.get("type"),
                            Defaults.DEFAULT_TYPE_NAME));
        } else {
            harvester.index(Defaults.DEFAULT_INDEX_NAME).type(Defaults.DEFAULT_TYPE_NAME);
        }
    }

    @Override
    public void start() {
        harvesterThread = EsExecutors
                .daemonThreadFactory(settings.globalSettings(),
                        "ubbRiver(" + riverName().name() + ")")
                .newThread(harvester);
        harvesterThread.start();
    }

    @Override
    public void close() {
        harvester.log("Closing UBB RDF river [" + riverName.name() + "]");
        harvester.setClose(true);
        if (harvesterThread != null && !harvesterThread.isInterrupted()) {
            harvesterThread.interrupt();
        }
    }
}
