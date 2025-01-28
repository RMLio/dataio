package be.ugent.idlab.knows.dataio.utils;

import net.snowflake.client.jdbc.internal.google.api.client.http.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpConnectTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * General static utility functions
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static InputStream getInputStreamFromURL(URL url, String contentType) throws IOException {
        return getInputStreamFromURL(url, contentType, Collections.emptyMap());
    }

    public static InputStream getInputStreamFromURL(URL url, String contentType, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", contentType);
        // Set encoding if not set before
        if (!headers.containsKey("charset")) {
            connection.setRequestProperty("charset", "utf-8");
        }
        // Apply all headers
        headers.forEach((name, value) -> {
            logger.debug("{}: {}", name, value);
            connection.setRequestProperty(name, value);
        });
        logger.debug("trying to connect");
        connection.connect();
        if (connection.getResponseCode() == 401) throw new IOException("HTTP 401: Not Authenticated");
        logger.debug("getting inputstream");
        InputStream inputStream = connection.getInputStream();
        logger.debug("got inputstream");
        return inputStream;
    }

    public static String getURLParamsString(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return !resultString.isEmpty()
                ? resultString.substring(0, resultString.length() - 1) // remove final '&'
                : resultString;
    }

    public static String hashCode(String s) {
        int hash = 0;
        for (int i = 0; i < s.toCharArray().length; i++) {
            hash += s.toCharArray()[i] * 31 ^ (s.toCharArray().length - 1 - i);
        }
        return Integer.toString(Math.abs(hash));
    }

    public static int getHashOfString(String str) {
        int hash = 7;

        for (int i = 0; i < str.length(); i++) {
            hash = hash * 31 + str.charAt(i);
        }

        return hash;
    }

    public static int getFreePortNumber() throws IOException {
        ServerSocket temp = new ServerSocket(0);
        temp.setReuseAddress(true);
        int portNumber = temp.getLocalPort();
        temp.close();
        return portNumber;
    }
}
