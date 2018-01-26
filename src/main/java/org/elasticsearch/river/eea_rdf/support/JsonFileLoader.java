package org.elasticsearch.river.eea_rdf.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.jena.util.FileManager;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.loader.JsonSettingsLoader;

import java.io.*;
import java.util.Collections;
import java.util.Map;

/**
 * A class that provides utility methods for loading JSON config file.
 * <p>
 *
 * @author Hemed Al Ruwehy
 *
 * 25.01.2018
 */
public class JsonFileLoader extends JsonSettingsLoader {
    private static final ESLogger logger = Loggers.getLogger(JsonFileLoader.class);


    /**
     * Read JSON file from URL
     *
     * @param url URL (file: or http: or anything a FileManager can handle)
     * @return returns a string representation of the file contents.
     */
    public static String read(String url) {
        return new FileManager().readWholeFileAsUTF8(url);
    }

    /**
     * Read JSON file from stream
     *
     * @param in input stream
     * @return returns a string representation of the file contents.
     */
    public static String read(InputStream in) {
        return new FileManager().readWholeFileAsUTF8(in);
    }

    /**
     * Read JSON file from stream and get file contents as strings
     *
     * @param filePath a file path
     * @return returns a string representation of the file contents.
     */
    public static String readFromFile(String filePath) {
        BufferedReader reader;
        JsonElement element;
        try {
              reader = new BufferedReader(new FileReader(filePath));
              element = new JsonParser().parse(reader);

        } catch (FileNotFoundException e) {
            logger.error("File path does not exist for " + filePath);
            throw new IllegalArgumentException("Make sure this path exist: " + filePath);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Malformed JSON in the file " + filePath);
        }

        return element.toString();
    }


    /**
     * Convert a JSON string to Java map
     *
     * @param source a valid JSON string
     * @return a Java map which is the result of JSON string
     *
     * @throws IOException
     */
    public Map<String, String> toMap(String source) throws IOException {
        return super.load(source);
    }


    /**
     * Resolve a given string to a Java Map.
     *
     * @param pathOrJsonString it may be a JSON file path or a JSON string
     */
    public Map<String, String> resolveToMap(String pathOrJsonString) {
        if (Strings.hasText(pathOrJsonString)) {
            try {
                //It is likely a JSON string if it starts with "{" and ends with "}"
                //if it is not valid JSON, exception will be thrown at the later stage
                if (pathOrJsonString.trim().startsWith("{") && pathOrJsonString.trim().endsWith("}")) {
                    return toMap(pathOrJsonString);
                }
                return toMap(readFromFile(pathOrJsonString));
            } catch (IOException e) {
                logger.warn("Unable to resolve path or JSON content [{ }] ", pathOrJsonString);
            }
        }
        return Collections.emptyMap();
    }

}
