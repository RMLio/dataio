package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static be.ugent.idlab.knows.dataio.utils.Utils.getHashOfString;

/**
 * This class represents the access to a SPARQL endpoint.
 */
public class SPARQLEndpointAccess implements Access {

    private static final long serialVersionUID = 7331603102049875880L;
    private String contentType;
    private String endpoint;
    private String query;

    /**
     * This constructor takes a content type, url of the endpoint, and a SPARQL query as arguments.
     *
     * @param contentType the content type of the results.
     * @param endpoint    the url of the SPARQL endpoint.
     * @param query       the SPARQL query used on the endpoint.
     */
    public SPARQLEndpointAccess(String contentType, String endpoint, String query) {
        this.contentType = contentType;
        this.endpoint = endpoint;
        this.query = query;
    }

    /**
     * Clean a SPARQLQuery by removing whitespaces or comments
     *
     * @param query The SPARQL query
     * @return The cleaned query
     */
    public static String cleanQuery(String query) {
        // Original, naive implementation that could lead to malformed queries
        return query.replaceAll("[\r\n]+", " ").trim();
    }

    /**
     * This method returns an InputStream of the results of the SPARQL endpoint.
     *
     * @return an InputStream.
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        // Query the endpoint
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Set 'Accept' header
        connection.setRequestProperty("Accept", contentType);

        // Set 'query' parameter
        Map<String, String> urlParams = new HashMap<String, String>() {{
            put("query", query);
        }};

        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(Utils.getURLParamsString(urlParams));
        out.flush();
        out.close();

        // TODO check this code
        int status = connection.getResponseCode();

        return connection.getInputStream();
    }

    @Override
    public InputStreamReader getInputStreamReader() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SPARQLEndpointAccess) {
            SPARQLEndpointAccess access = (SPARQLEndpointAccess) o;
            return endpoint.equals(access.getEndpoint()) && contentType.equals(access.getContentType()) && query.equals(access.getQuery());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getHashOfString(getEndpoint() + getQuery() + getContentType());
    }

    /**
     * This methods returns the datatypes of the results of the SPARQL query.
     * This method always returns null at the moment.
     *
     * @return the datatypes of the results of the SPARQL query.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return null;
    }

    /**
     * This method returns the content type of the results.
     *
     * @return the content type of the results.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Path to the resource the Access represents, be it the URL, remote address, filepath...
     */
    @Override
    public String getAccessPath() {
        return this.endpoint;
    }

    /**
     * This method returns the url of the endpoint.
     *
     * @return the url of the endpoint.
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * This method returns the SPARQL query that is used to get the results.
     *
     * @return the SPARQL query that is used to get the results.
     */
    public String getQuery() {
        return query;
    }
}
