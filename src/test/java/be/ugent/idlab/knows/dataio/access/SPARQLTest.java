package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import net.minidev.json.JSONArray;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SPARQLTest {
    @Test
    public void testRemote() throws SQLException, IOException {
        String resource = "src/test/resources/sparql/resource.ttl";
        FusekiServer server = FusekiServer.create()
                .add("/ds1", RDFDataMgr.loadDataset(resource))
                .build();
        server.start();

        // actual test
        String endpoint = String.format("%sds1/sparql", server.serverURL());
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "            PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "            SELECT ?fname ?lname ?amount\n" +
                "            WHERE {\n" +
                "                ?x  foaf:firstName ?fname ;\n" +
                "                    foaf:lastName  ?lname ;\n" +
                "                    <http://example.com/amount> ?amount . }";


        Access access = new SPARQLEndpointAccess("application/sparql-results+json", endpoint, query);
        Object actual = JSONSourceIterator.getDocumentFromStream(access.getInputStream());

        runSparqlTest(actual);

        server.stop();
    }

    @Test
    public void testLocal() throws IOException, SQLException {
        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "            PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "            SELECT ?fname ?lname ?amount\n" +
                "            WHERE {\n" +
                "                ?x  foaf:firstName ?fname ;\n" +
                "                    foaf:lastName  ?lname ;\n" +
                "                    <http://example.com/amount> ?amount . }";

        try (SPARQLLocalFileAccess access = new SPARQLLocalFileAccess("src/test/resources/sparql/resource.ttl", query, "application/sparql-results+json")) {
            Object actual = JSONSourceIterator.getDocumentFromStream(access.getInputStream());
            runSparqlTest(actual);
        }
    }

    private void runSparqlTest(Object actual) {
        // set up the expected object, bottom up
        Map<String, String>
                bob = new LinkedHashMap<>() {{
            put("type", "literal");
            put("value", "Bob");
        }},
                smith = new LinkedHashMap<>() {{
                    put("type", "literal");
                    put("value", "Smith");
                }},
                amount = new LinkedHashMap<>() {{
                    put("type", "literal");
                    put("value", "30");
                }};

        Map<String, Map<String, String>> map = new LinkedHashMap<>() {{
            put("fname", bob);
            put("lname", smith);
            put("amount", amount);
        }};

        JSONArray array = new JSONArray();
        array.add(map);

        Map<String, JSONArray> results = new LinkedHashMap<>() {{
            put("bindings", array);
        }};

        // prepare header
        JSONArray varsArray = new JSONArray();
        varsArray.addAll(List.of("fname", "lname", "amount"));

        Map<String, JSONArray> vars = new LinkedHashMap<>() {{
            put("vars", varsArray);
        }};

        Map<String, Map<String, JSONArray>> expected = new LinkedHashMap<>() {{
            put("head", vars);
            put("results", results);
        }};

        assertEquals(expected, actual);
    }
}
