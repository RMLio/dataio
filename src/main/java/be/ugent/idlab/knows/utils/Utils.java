package be.ugent.idlab.knows.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * General static utility functions
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static final Map<String,String> typeToEncoding = Map.of(
            "csv", "utf-8",
            "csvw", "utf-8",
            "xlsx", "xlsx",
            "html", "utf-8",
            "json", "utf-8",
            "ods", "ods",
            "xml", "utf-8"
    );


    public static InputStream getInputStreamFromFile(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static int getHashOfString(String str) {
        int hash = 7;

        for (int i = 0; i < str.length(); i++) {
            hash = hash * 31 + str.charAt(i);
        }

        return hash;
    }

    public static InputStream getInputStreamFromURL(URL url, String contentType) {
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", contentType);
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            inputStream = connection.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return inputStream;
    }

    public static String getURLParamsString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1) // remove final '&'
                : resultString;
    }

    public static InputStream getInputStreamFromURL(URL url, String contentType, HashMap<String, String> headers) {
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", contentType);
            // Set encoding if not set before
            if(!headers.containsKey("charset")) {
                headers.put("charset", "utf-8");
            }
            // Apply all headers
            headers.forEach((name, value) -> {
                logger.debug("{}: {}", name, value);
                connection.setRequestProperty(name, value);
            });
            logger.debug("trying to connect");
            connection.connect();
            logger.debug("getting inputstream");
            inputStream = connection.getInputStream();
            logger.debug("got inputstream");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return inputStream;
    }
}
