package org.elasticsearch.river.ubb.utils;


import org.apache.jena.atlas.web.HttpException;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.InputStream;

/**
 * Utility class which contains convenient methods for managing files
 *
 * @author Hemed Al Ruwehy
 */
public class FileManager {
    private static final ESLogger logger = Loggers.getLogger(FileManager.class);

    /**
     * Prevent instantiability
     */
    private FileManager() {
    }

    /**
     * Reads file from URL for a given number of retries
     *
     * @param url           URL (file: or http: or anything a FileManager can handle)
     * @param numberOfRetry how many times should a method retry if HTTP server encountered internal error
     * @return returns a string representation of the file contents
     */
    public static String read(String url, int numberOfRetry) {
        boolean retry;
        int countRetry = 0;
        String content = "";
        do {
            retry = false;
            try {
                content = read(url);
            } catch (HttpException httpe) {
                if (httpe.getResponseCode() >= 500) {
                    logger.error("Encountered an internal server error for URL [{}]. " +
                            "Retrying...", url);
                    retry = true;
                    countRetry++;
                } else {
                    throw httpe;
                }
            }
            if (countRetry > numberOfRetry) {
                throw new HttpException("Resource unavailable at: " + url);
            }
        } while (retry);

        return content;
    }

    /**
     * Reads file from URL
     *
     * @param url URL (file: or http: or anything a FileManager can handle)
     * @return returns a string representation of the file contents.
     */
    public static String read(String url) {
        return org.apache.jena.util.FileManager.get().readWholeFileAsUTF8(url);
    }

    /**
     * Reads JSON file from stream
     *
     * @param in input stream
     * @return returns a string representation of the file contents.
     */
    public static String read(InputStream in) {
        return org.apache.jena.util.FileManager.get().readWholeFileAsUTF8(in);
    }
}
