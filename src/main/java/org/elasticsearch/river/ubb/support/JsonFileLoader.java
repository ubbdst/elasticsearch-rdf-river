package org.elasticsearch.river.ubb.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.loader.JsonSettingsLoader;
import org.elasticsearch.river.ubb.utils.FileManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * A class that provides utility methods for loading JSON config file.
 * <p>
 *
 * @author Hemed Al Ruwehy
 * <p>
 * 25.01.2018
 */
public class JsonFileLoader extends JsonSettingsLoader {
    private static final ESLogger logger = Loggers.getLogger(JsonFileLoader.class);

    /**
     * Parse a given string to JSON
     */
    public static JsonElement parseJson(String s) {
        return new JsonParser().parse(s);
    }

    /**
     * Convert a JSON string to a flat Java map
     *
     * @param source a valid JSON string
     * @return a Java map which is the result of JSON string
     * @throws IOException
     */
    public Map<String, String> toFlatMap(String source) throws IOException {
        return super.load(source);
    }

    /**
     * Resolve a given string to a Java Map.
     *
     * @param pathOrJsonString it may be a JSON file path/url or a JSON string
     */
    public Map<String, String> resolveToFlatMap(String pathOrJsonString) {
        if (Strings.hasText(pathOrJsonString)) {
            try {
                return toFlatMap(resolveToString(pathOrJsonString));
            } catch (IOException e) {
                logger.warn("Unable to resolve path or JSON content [{ }] ", pathOrJsonString);
            }
        }
        return Collections.emptyMap();
    }


    /**
     * Resolve a given content to string
     */
    public String resolveToString(String content) {
        if (content != null && !content.isEmpty()) {
            //It is likely a JSON string if it starts with "{" and ends with "}"
            //if it is not valid JSON, exception will be thrown at the later stage
            if (content.trim().startsWith("{") && content.trim().endsWith("}")) {
                return content;
            }
            return FileManager.readAsUTF8(content, 20);
        }
        return "";
    }


}
