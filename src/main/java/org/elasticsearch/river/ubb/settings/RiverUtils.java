package org.elasticsearch.river.ubb.settings;

import java.util.regex.Pattern;

public class RiverUtils {

    //Empty character
    private final static char EMPTY_CHAR = ' ';

    //Elasticsearch reserved characters (without minus sign and forward slash)
    private static final char[] SPECIAL_CHARS = {
            '*', '"', '\\', '=', '&', '|', '>', '<', '!', '(', ')',
            '{', '}', '^', '~', '?', ':', '!', '[', ']', '.'
    };

    /**
     * Removes special characters from the given string
     *
     * @param s a given string
     * @return this string where all special characters, if exist, have been removed
     */
    public static String removeSpecialChars(String s) {
        if (isNullOrEmpty(s)) {
            return s;
        }
        for (char character : SPECIAL_CHARS) {
            if (s.indexOf(character) > -1) {
                s = s.replace(character, EMPTY_CHAR);
            }
        }
        return s.trim();
    }


    public static String parseForJson(String text) {
        return text.trim().replaceAll("[\n\r]", " ")
                .replace('\"', '\'')
                .replace("\t", "    ")
                .replace("\\'", "\'")
                .replaceAll("\\\\x[a-fA-F0-9][a-fA-F0-9]", "_")
                .replace("\\", "\\\\");
    }

    /**
     * Checks if a given string is either null or empty
     *
     * @param s a string to check
     * @return true if this string is null or empty, otherwise false
     */
    public static boolean isNullOrEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * Gets time representation as string
     */
    public static String getTimeFormatAsString(long timeInMilliSeconds) {
        //Time in seconds
        double timeInSeconds = timeInMilliSeconds / 1000.00;
        //In minutes
        if (timeInSeconds >= 60 && timeInSeconds < 60 * 60) {
            return timeInSeconds / 60 + " minutes";
        }
        //In hours
        if (timeInSeconds >= 60 * 60 && timeInSeconds < 24 * 3600) {
            return timeInSeconds / 3600 + " hours";
        }
        //default unit
        return timeInSeconds + " seconds";
    }


    /**
     * Gets label coalesce to be used for sorting
     *
     * @param uri a URI for the label
     * @param labelValue a lexical value of the label
     */
    public static String getLabelCoalesce(String uri, String labelValue) {
        for (String labelUri : Settings.SORT_LABELS) {
            if (uri.equals(labelUri) && !labelValue.isEmpty()) {
                return removeSpecialChars(labelValue);
            }
        }
        return "";
    }


    /**
     * Removes illegal XML characters
     */
    public static String removeIllegalXMLChar(String text) {
        Pattern invalidXMLChars = Pattern.compile("[\\x00-\\x08\\x0b\\x0c\\x0e-\\x1F]");
        invalidXMLChars.matcher(text).replaceAll("");
        return text;
    }
}
