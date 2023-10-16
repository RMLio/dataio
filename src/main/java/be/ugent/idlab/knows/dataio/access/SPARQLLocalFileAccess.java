package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.utils.Utils;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.riot.RDFDataMgr;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Access used to perform SPARQL querying of a local file, using a local Fuseki server.
 * Combines the functionality of LocalFileAccess and SPARQLEndpointAccess.
 */
public class SPARQLLocalFileAccess implements Access, AutoCloseable {
    private static final long serialVersionUID = -4392563969906913155L;
    private final String query;
    private final FusekiServer server;
    private final String contentType;

    /**
     * Construct the access by starting a local Fuseki server on a free port.
     * This port is only used internally and will be released once the Access is closed.
     *
     * @param pathToFile path to the file to run SPARQL query against.
     * @param query SPARQL query to run against the file
     * @param contentType content type for the expected response
     * @throws IOException will be thrown if it occurs during opening or closing the socket.
     */
    public SPARQLLocalFileAccess(String pathToFile, String query, String contentType) throws IOException {
        this.query = query;
        this.contentType = contentType;
        int port = Utils.getFreePortNumber();

        this.server = FusekiServer.create()
                .port(port)
                .add("data", RDFDataMgr.loadDataset(pathToFile))
                .build();
        this.server.start();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URL url = new URL(String.format("%sdata", this.server.serverURL()));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.setRequestProperty("Accept", this.contentType);


        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        Map<String, String> urlParams = new HashMap<>() {{
            put("query", query);
        }};
        out.writeBytes(Utils.getURLParamsString(urlParams));
        out.flush();
        out.close();

        return conn.getInputStream();
    }

    @Override
    public InputStreamReader getInputStreamReader() throws IOException {
        return new InputStreamReader(this.getInputStream());
    }

    @Override
    public Map<String, String> getDataTypes() {
        return null;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public String getAccessPath() {
        return String.format("%sdata/sparql", this.server.serverURL());
    }

    @Override
    public void close() {
        this.server.stop();
    }
}
