package be.ugent.idlab.knows.access;

import be.ugent.idlab.knows.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.utils.Utils;
import net.minidev.json.JSONArray;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SPARQLTest {

    private static int PORTNUMBER;
    private FusekiServer server;

    @BeforeAll
    public static void beforeClass() {
        try {
            PORTNUMBER = Utils.getFreePortNumber();
        } catch (IOException e) {
            throw new RuntimeException("Could not find a free port number for SPARQL testing.");
        }
    }

    @BeforeEach
    public void before() {
        String resource = "src/test/resources/sparql/resource.ttl";
        this.server = FusekiServer.create()
                .port(PORTNUMBER)
                .add("/ds1", RDFDataMgr.loadDataset(resource))
                .build();

        this.server.start();
    }

    @Test
    public void test() throws SQLException, IOException, ClassNotFoundException {
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

        // actual test
        String endpoint = String.format("http://localhost:%d/ds1/sparql", PORTNUMBER);
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

        assertEquals(expected, actual);
    }

    @After
    public void after() {
        this.server.stop();
    }
}
