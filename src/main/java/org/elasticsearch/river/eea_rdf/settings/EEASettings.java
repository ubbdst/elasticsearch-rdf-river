package org.elasticsearch.river.eea_rdf.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author EEA
 * Modified by Hemed Ali, 09-03-2015
 */
public abstract class EEASettings {

    public final static String RIVER_SETTINGS_KEY = "eeaRDF";
    public final static String UBB_SETTINGS_KEY = "ubbRDF";
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
    public final static String[] DEFAULT_URI_DESCRIPTION = {"http://www.w3.org/2000/01/rdf-schema#label"};
    public final static String DEFAULT_SYNC_COND = "";
    public final static String DEFAULT_SYNC_TIME_PROP =
            "http://cr.eionet.europa.eu/ontologies/contreg.rdf#lastRefreshed";
    public final static Boolean DEFAULT_SYNC_OLD_DATA = false;

    public static String parseForJson(String text) {
        return text.trim().replaceAll("[\n\r]", " ")
                .replace('\"', '\'')
                .replace("\t", "    ")
                .replace("\\'", "\'")
                .replaceAll("\\\\x[a-fA-F0-9][a-fA-F0-9]", "_")
                .replace("\\", "\\\\");
    }

    public static String removeIllegalXMLChar(String text) {
        Pattern invalidXMLChars = Pattern.compile("[\\x00-\\x08\\x0b\\x0c\\x0e-\\x1F]");
        invalidXMLChars.matcher(text).replaceAll("");
        return text;
    }

    /**
     * Gets time representation as string
     */
    public static String getTimeFormatAsString(long timeInMilliSeconds) {

        //Time in seconds
        double timeInSeconds = timeInMilliSeconds/1000.0;

            //In minutes
            if(timeInSeconds >= 60 && timeInSeconds < 60*60){
                return timeInSeconds/60 + " minutes";
            }

            //In hours
            if(timeInSeconds >= 60*60 && timeInSeconds < 24*3600) {
                return timeInSeconds/3600 + " hours";
            }

            //default unit
            return timeInSeconds + " seconds";
    }
}
