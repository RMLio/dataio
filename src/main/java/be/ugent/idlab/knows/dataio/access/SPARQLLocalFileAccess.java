package be.ugent.idlab.knows.dataio.access;

import org.apache.jena.query.*;
import org.apache.jena.riot.RDFDataMgr;

import java.io.*;
import java.util.Map;

/**
 * Allows querying a local RDF file using SPARQL
 */
public class SPARQLLocalFileAccess implements Access, AutoCloseable {
    @Serial
    private static final long serialVersionUID = -4392563969906913155L;
    private final String query;
    private final String pathToFile;
    private final String contentType;

    /**
     * Local RDF file access constructor
     *
     * @param pathToFile path to the file to run SPARQL query against.
     * @param query SPARQL query to run against the file
     * @param contentType content type for the expected response
     */
    public SPARQLLocalFileAccess(String pathToFile, String query, String contentType) {
        this.pathToFile = pathToFile;
        this.query = query;
        this.contentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // Load file into RDF dataset
        Dataset data = RDFDataMgr.loadDataset(pathToFile);

        // Execute the query
        try (QueryExecution qexec = QueryExecutionFactory.create(query, data)) {
            final ResultSet results = qexec.execSelect();

            // Copy results into byte array, which will be passed to an InputStream.
            // A PipedInputSteam approach doesn't seem to work due to the internals of ResultSetFormatter
            try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                if (contentType.contains("json")) {
                    ResultSetFormatter.outputAsJSON(out, results);
                } else if (contentType.contains("xml")) {
                    ResultSetFormatter.outputAsXML(out, results);
                } else { // output CSV by default
                    ResultSetFormatter.outputAsCSV(out, results);
                }
                return new ByteArrayInputStream(out.toByteArray());
            }
        }
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
        return pathToFile;
    }

    @Override
    public void close() {
        // do nothing
    }
}
