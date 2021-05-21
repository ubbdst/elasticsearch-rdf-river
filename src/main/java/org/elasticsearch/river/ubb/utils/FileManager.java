package org.elasticsearch.river.ubb.utils;


import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.shared.WrappedIOException;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Utility class which contains convenient methods for managing files
 *
 * @author Hemed Al Ruwehy
 */
public class FileManager {
    public static final Charset CP_1252 = Charset.forName("cp1252");
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
    public static String readAsUTF8(String url, int numberOfRetry) {
        boolean retry;
        int countRetry = 0;
        String content = "";
        do {
            retry = false;
            try {
                content = readAsUTF8(url);
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
    public static String readAsUTF8(String url) {
        return org.apache.jena.util.FileManager.get().readWholeFileAsUTF8(url);
    }

    /**
     * Reads JSON file from stream
     *
     * @param in input stream
     * @return returns a string representation of the file contents.
     */
    public static String readAsUTF8(InputStream in) {
        return org.apache.jena.util.FileManager.get().readWholeFileAsUTF8(in);
    }

    /**
     * Opens stream for a given file or url
     */
    public static InputStream open(String fileOrUri) throws FileNotFoundException {
        InputStream in = org.apache.jena.util.FileManager.get().open(fileOrUri);
        if (Objects.isNull(in)) {
            throw new FileNotFoundException("File Not Found: " + fileOrUri);
        }
        return in;
    }

    /**
     * Reads whole file as CP1252 (due to issue from OCR scan)
     *
     * @param fileOrUri file name or URI
     * @return returns a string representation of the file contents.
     */
    public static String readAsCP1252(String fileOrUri) {
        try (Reader r = new InputStreamReader(open(fileOrUri), CP_1252);
             StringWriter sw = new StringWriter(1024)) {
            char buff[] = new char[1024];
            while (true) {
                int l = r.read(buff);
                if (l <= 0)
                    break;
                sw.write(buff, 0, l);
            }
            return sw.toString();
        } catch (IOException ex) {
            throw new WrappedIOException(ex);
        }
    }

    /**
     * Reads content from a given url
     *
     * @param url a url to fetch url file content from
     *
     * @return a file content or the same url if
     */
    public static String readUrlContent (String url) {
        try {
            if (logger.isDebugEnabled()) {
                logger.info("Reading URL content from: " + url);
            }
            String urlContent;
            try {
                urlContent = FileManager.readAsUTF8(url, 5);
            } catch (org.apache.jena.shared.WrappedIOException ex) {
                //Retry with CP1252
                if (logger.isDebugEnabled()) {
                    logger.warn("Cannot read {} using UTF-8 due to [{}], retrying with CP1252",
                            url, ex.getLocalizedMessage());
                }
                urlContent = FileManager.readAsCP1252(url);
            }
            return urlContent;
        } catch (Exception e) {
            logger.error("Cannot read content from {} due to {}", url, e.getLocalizedMessage());
            e.printStackTrace();
        }
        return url;
    }
}

