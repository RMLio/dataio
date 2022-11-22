package be.ugent.idlab.knows.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * General static utility functions
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    // Without support for custom registered languages of length 5-8 of the IANA language-subtag-registry
    private static final Pattern regexPatternLanguageTag = Pattern.compile("^((?:(en-GB-oed|i-ami|i-bnn|i-default|i-enochian|i-hak|i-klingon|i-lux|i-mingo|i-navajo|i-pwn|i-tao|i-tay|i-tsu|sgn-BE-FR|sgn-BE-NL|sgn-CH-DE)|(art-lojban|cel-gaulish|no-bok|no-nyn|zh-guoyu|zh-hakka|zh-min|zh-min-nan|zh-xiang))|((?:([A-Za-z]{2,3}(-(?:[A-Za-z]{3}(-[A-Za-z]{3}){0,2}))?)|[A-Za-z]{4})(-(?:[A-Za-z]{4}))?(-(?:[A-Za-z]{2}|[0-9]{3}))?(-(?:[A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3}))*(-(?:[0-9A-WY-Za-wy-z](-[A-Za-z0-9]{2,8})+))*(-(?:x(-[A-Za-z0-9]{1,8})+))?)|(?:x(-[A-Za-z0-9]{1,8})+))$");

    public static final Map<String,String> typeToEncoding = Map.of(
            "csv", "utf-8",
            "csvw", "utf-8",
            "xlsx", "xlsx",
            "html", "utf-8",
            "json", "utf-8",
            "ods", "ods",
            "xml", "utf-8"
    );

    public static Reader getReaderFromLocation(String location, File basePath, String contentType) throws IOException {
        if (isRemoteFile(location)) {
            try {
                return getReaderFromURL(new URL(location), contentType);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return getReaderFromFile(getFile(location, basePath));
        }
    }

    public static InputStream getInputStreamFromLocation(String location, File basePath, String contentType) throws IOException {
        return getInputStreamFromLocation(location, basePath, contentType, new HashMap<String, String>());
    }

    public static InputStream getInputStreamFromLocation(String location, File basePath, String contentType, HashMap<String, String> headers) throws IOException {
        if (isRemoteFile(location)) {
            return getInputStreamFromURL(new URL(location), contentType, headers);
        } else {
            return getInputStreamFromFile(getFile(location, basePath));
        }
    }

    public static File getFile(String path) throws IOException {
        return Utils.getFile(path, null);
    }

    public static File getFile(String path, File basePath) throws IOException {
        // Absolute path?
        File f = new File(path);
        if (f.isAbsolute()) {
            if (f.exists()) {
                return f;
            } else {
                throw new FileNotFoundException();
            }
        }

        if (basePath == null) {
            try {
                basePath = new File(System.getProperty("user.dir"));
            } catch (Exception e) {
                throw new FileNotFoundException();
            }
        }

        logger.debug("Looking for file {} in basePath {}", path, basePath);

        // Relative from user dir?
        f = new File(basePath, path);
        if (f.exists()) {
            return f;
        }

        logger.debug("File {} not found in {}", path, basePath);
        logger.debug("Looking for file {} in {} /../", path, basePath);


        // Relative from parent of user dir?
        f = new File(basePath, "../" + path);
        if (f.exists()) {
            return f;
        }

        logger.debug("File {} not found in {}", path, basePath);

        logger.debug("Looking for file {} in the resources directory", path);

        // Resource path?
        try {
            return MyFileUtils.getResourceAsFile(path);
        } catch (IOException e) {
            // Too bad
        }

        logger.debug("File {} not found in the resources directory", path);

        throw new FileNotFoundException(path);
    }

    public static Reader getReaderFromURL(URL url) throws IOException {
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }

    public static Reader getReaderFromURL(URL url, String contentType) throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStreamFromURL(url, contentType)));
    }

    public static Reader getReaderFromFile(File file) throws FileNotFoundException {
        return new FileReader(file);
    }

    public static InputStream getInputStreamFromURL(URL url) throws IOException {
        return url.openStream();
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

    public static InputStream getInputStreamFromURL(URL url, String contentType, Map<String, String> headers) {
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", contentType);
            // Set encoding if not set before
            if (!headers.containsKey("charset")) {
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

    public static InputStream getInputStreamFromAuthURL(URL url, String contentType, Map<String, String> headers) throws Exception {
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", contentType);
            // Set encoding if not set before
            if (!headers.containsKey("charset")) {
                headers.put("charset", "utf-8");
            }
            // Apply all headers
            headers.forEach((name, value) -> {
                logger.debug("{}: {}", name, value);
                connection.setRequestProperty(name, value);
            });
            logger.debug("trying to connect");
            connection.connect();
            if (connection.getResponseCode() == 401) throw new Exception("not authenticated");
            logger.debug("getting inputstream");
            inputStream = connection.getInputStream();
            logger.debug("got inputstream");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return inputStream;
    }

    public static InputStream getInputStreamFromFile(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static InputStream getPostRequestResponse(URL url, String contentType, byte[] auth) {
        InputStream inputStream = null;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("charset", "utf-8");

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", contentType);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(auth);
            inputStream = connection.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return inputStream;
    }

    public static boolean isRemoteFile(String location) {
        return location.startsWith("https://") || location.startsWith("http://");
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
        return resultString.length() > 0
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

    public static String fileToString(File file) throws IOException {
        Reader reader = getReaderFromFile(file);
        int intValueOfChar;
        StringBuilder targetString = new StringBuilder();
        while ((intValueOfChar = reader.read()) != -1) {
            targetString.append((char) intValueOfChar);
        }
        reader.close();
        return targetString.toString();
    }
}
