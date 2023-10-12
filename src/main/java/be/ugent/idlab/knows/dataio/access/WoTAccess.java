package be.ugent.idlab.knows.dataio.access;

import com.jayway.jsonpath.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;
import static be.ugent.idlab.knows.dataio.utils.Utils.getInputStreamFromURL;


public class WoTAccess implements Access {

    private static final Logger logger = LoggerFactory.getLogger(WoTAccess.class);
    private static final long serialVersionUID = -1098654761923880385L;
    private final Map<String, Map<String, String>> auth;
    private final String location;
    private final String contentType;
    private final Map<String, String> headers;

    /**
     * This constructor of WoTAccess taking location and content type as arguments.
     *
     * @param location    the location of the WoT Thing.
     * @param contentType the content type of the WoT Thing.
     */
    public WoTAccess(String location, String contentType, Map<String, String> headers, Map<String, Map<String, String>> auth) {
        this.location = location;
        this.contentType = contentType;
        this.headers = headers;
        this.auth = auth;

        logger.debug("Created WoTAccess:\n\tlocation: {}\n\tcontent-type: {}", this.location, this.contentType);
        logger.debug(headers.toString());
        headers.forEach((name, value) -> {
            logger.debug("Header: {} : {}", name, value);
        });
    }

    @Override
    public InputStream getInputStream() throws IOException {
        logger.debug("get inputstream");
        InputStream response;

        if (auth.get("data").containsKey("refresh")) {
            try {
                response = getInputStreamFromAuthURL(new URL(location), contentType, headers);
            } catch (Exception e) {
                logger.debug("Refresh token");
                refreshToken();
                logger.debug("try again with new token");
                logger.debug("new token = {}", this.headers.get(this.auth.get("info").get("name")));
                return getInputStreamFromURL(new URL(location), contentType, headers);
            }
        } else {
            response = getInputStreamFromURL(new URL(location), contentType, headers);
        }
        return response;
    }

    @Override
    public InputStreamReader getInputStreamReader() {
        return null;
    }

    /**
     * This methods returns the datatypes of the WoT Thing.
     * This method always returns null, because the datatypes can't be determined from a WoT Thing for the moment.
     *
     * @return the datatypes of the file.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WoTAccess) {
            WoTAccess access = (WoTAccess) o;
            return location.equals(access.getLocation()) && contentType.equals(access.getContentType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getHashOfString(getLocation() + getContentType());
    }

    /**
     * The method returns the location of the remote file.
     *
     * @return the location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method returns the content type of the remote file.
     *
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        return this.location;
    }

    public void refreshToken() throws MalformedURLException {
        StringBuilder data = new StringBuilder();
        data.append("{\"grant_type\": \"refresh_token\"");
        for (String name : auth.get("data").keySet()) {
            data.append(" ,\"").append(name).append("\":\"").append(auth.get("data").get(name)).append("\"");
        }
        data.append("}");
        logger.debug(data.toString());
        InputStream response = getPostRequestResponse(new URL(auth.get("info").get("authorization")), contentType, data.toString().getBytes());
        HashMap<String, String> jsonResponse = (HashMap<String, String>) Configuration.defaultConfiguration().jsonProvider().parse(response, "utf-8");
        this.headers.put(auth.get("info").get("name"), "Bearer " + jsonResponse.get("access_token"));
    }

    private InputStream getPostRequestResponse(URL url, String contentType, byte[] auth) {
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

    private InputStream getInputStreamFromAuthURL(URL url, String contentType, Map<String, String> headers) throws Exception {
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
}