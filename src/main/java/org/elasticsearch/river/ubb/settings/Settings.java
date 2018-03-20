package org.elasticsearch.river.ubb.settings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author European Environment Agency (EEA)
 * @author Hemed Al Ruwehy
 * Modified by Hemed Ali, 09-03-2015
 */
public final class Settings {

    public final static String RIVER_SETTINGS_KEY = "eeaRDF";
    public final static String UBB_SETTINGS_KEY = "ubbRiver";
    public final static String DEFAULT_INDEX_NAME = "rdfdata";
    public final static String DEFAULT_TYPE_NAME = "resource";
    public final static String SUGGESTION_FIELD = "suggest";
    public final static String RIVER_PLUGIN_NAME = "ubb-rdf-river";
    public final static String SUGGESTION_INPUT_FIELD = "input";
    public final static String SUGGESTION_OUTPUT_FIELD = "output";
    public final static String SUGGESTION_PAYLOAD_FIELD = "payload";
    public final static int DEFAULT_NUMBER_OF_BULK_ACTIONS = 100;
    public final static int DEFAULT_MAX_SUGGEST_INPUT_LENGTH = 50;
    public final static int DEFAULT_NUMBER_OF_RETRY = 5;
    public final static int DEFAULT_BULK_REQ = 30;
    public final static List<String> DEFAULT_QUERIES = new ArrayList<>();
    public final static String DEFAULT_QUERYTYPE = "construct";
    public final static String DEFAULT_PROPLIST = "[" +
            "\"http://purl.org/dc/terms/spatial\", " +
            "\"http://purl.org/dc/terms/creator\", " +
            "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\", " +
            "\"http://purl.org/dc/terms/issued\", " +
            "\"http://purl.org/dc/terms/title\", " +
            "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#about\", " +
            "\"language\", \"topic\"]";
    public final static String DEFAULT_SUGGEST_PROP_LIST = "[" +
            "\"http://purl.org/dc/terms/spatial\", " +
            "\"http://purl.org/dc/terms/creator\", " +
            "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\", " +
            "\"http://www.w3.org/1999/02/22-rdf-syntax-ns#about\"" + "]";
    public final static String DEFAULT_RESOURCE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#about";
    public final static String DEFAULT_LIST_TYPE = "white";
    public final static Boolean DEFAULT_ADD_LANGUAGE = true;
    public final static String DEFAULT_LANGUAGE = "no";
    public final static Boolean DEFAULT_ADD_URI = true;
    public final static Boolean DEFAULT_UPDATE_DOCUMENTS = false;
    public final static String[] DEFAULT_URI_DESCRIPTION = {
            "http://www.w3.org/2000/01/rdf-schema#label"
    };
    public final static String DEFAULT_SYNC_COND = "";
    public final static String DEFAULT_SYNC_TIME_PROP = "http://cr.eionet.europa.eu/ontologies/contreg.rdf#lastRefreshed";
    public final static Boolean DEFAULT_SYNC_OLD_DATA = false;
    public final static long DEFAULT_QUERY_LIMIT = 1000;
    public static final String SORT_LABEL_NAME = "labelSort";

    //In order of priority
    public static final String[] SORT_LABELS = {
            "http://purl.org/dc/terms/title",
            "http://www.w3.org/2004/02/skos/core#prefLabel",
            "http://xmlns.com/foaf/0.1/name",
            "http://www.w3.org/2000/01/rdf-schema#label"
    };



}
