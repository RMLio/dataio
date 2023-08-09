package be.ugent.idlab.knows.dataio.access;

import be.ugent.idlab.knows.dataio.access.utils.WoT_Handlers;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.utils.Utils;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WoTTest {

    private final Map<String, Map<String, String>> default_auth = new HashMap<>() {{
        put("data", new HashMap<>());
        put("info", new HashMap<>());
    }};

    private void runTest(int port, Map<String, HttpHandler> paths, Map<String, String> headers, Map<String, Map<String, String>> auth, String inputFile, String iterator, String entrypoint) {
        try {
            HttpServer server = WoT_Handlers.getServer(port, paths);

            String target = String.format("http://localhost:%d/%s", server.getAddress().getPort(), entrypoint);

            Access wot = new WoTAccess(target, "application/json", headers, auth);
            Access local = new LocalFileAccess("", inputFile, "json", "utf-8");

            List<Source> expected = getJsonSources(local, iterator);
            List<Source> actual = getJsonSources(wot, iterator);

            assertEquals(expected, actual);

            server.stop(0);

        } catch (IOException e) {
            System.err.println("An exception was thrown during the test: " + e);
            fail();
        }
    }

    /*
     * Most tests don't care about what port they're being run on, for these we choose a random port
     */
    private void runTest(Map<String, HttpHandler> paths, Map<String, String> headers, Map<String, Map<String, String>> auth, String inputFile, String iterator, String entrypoint) {
        try {
            int port = Utils.getFreePortNumber();
            runTest(port, paths, headers, auth, inputFile, iterator, entrypoint);
        } catch (IOException e) {
            System.err.println("An exception was thrown during the test: " + e);
            fail();
        }
    }

    @Test
    public void evaluate_essense_wot_support() {
        Map<String, HttpHandler> paths = Map.of("/trashcans", new WoT_Handlers.TrashCansFileHandler());

        Map<String, String> headers = new HashMap<>() {{
            put("apikey", "123456789");
            put("User-Agent", "RML Processor");
        }};

        runTest(paths, headers, default_auth, "src/test/resources/wot/iot-sensors.json", "$.[*]", "trashcans");
    }

    @Test
    public void evaluate_irail_stations_wot_support() {
        Map<String, HttpHandler> paths = Map.of("/redirect", new WoT_Handlers.IRailStationHandler1(), "/stations", new WoT_Handlers.IRailStationHandler2());

        Map<String, String> headers = new HashMap<>() {{
            put("User-Agent", "RMLMapper");
        }};

        runTest(paths, headers, default_auth, "src/test/resources/wot/stations.json", "$.station.[*]", "redirect");
    }

    @Test
    public void evaluate_bearer_authentication() {
        Map<String, HttpHandler> paths = Map.of("/api", new WoT_Handlers.BearerAuthenticationHandler());

        Map<String, String> headers = new HashMap<>() {{
            put("Authorization", "Bearer s3cr3tb34r3r");
            put("User-Agent", "RML Processor");
        }};

        runTest(paths, headers, default_auth, "src/test/resources/wot/bearer_security_input.json", "$.connections[*]", "api");
    }

    @Test
    public void evaluate_oauth_no_refresh() {
        try {
            int port = Utils.getFreePortNumber();

            Map<String, HttpHandler> paths = Map.of("/api", new WoT_Handlers.OAuthHandler());

            Map<String, String> headers = new HashMap<>() {{
                put("Authorization", "Bearer s3cr3tb34r3r");
                put("User-Agent", "RML Processor");
            }};

            Map<String, String> data = new HashMap<>() {{
                put("refresh", "xur2saef4s");
                put("client_secret", "testSecret");
                put("client_id", "testID");
            }}, info = new HashMap<>() {{
                put("authorization", String.format("http://localhost:%d/auth", port));
                put("name", "Authorization");
            }};

            Map<String, Map<String, String>> auth = new HashMap<>() {{
                put("data", data);
                put("info", info);
            }};

            runTest(port, paths, headers, auth, "src/test/resources/wot/oauth_input.json", "$.connections[*]", "api");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void evaluate_oauth_refresh() {
        try {
            int port = Utils.getFreePortNumber();

            Map<String, HttpHandler> paths = Map.of("/api", new WoT_Handlers.OAuthRefreshApi(), "/auth", new WoT_Handlers.OAuthRefreshAuth());

            Map<String, String> data = new HashMap<>() {{
                put("refresh", "xur2saef4s");
                put("client_secret", "testSecret");
                put("client_id", "testID");
            }}, info = new HashMap<>() {{
                put("authorization", String.format("http://localhost:%d/auth", port));
                put("name", "Authorization");
            }};

            Map<String, Map<String, String>> auth = new HashMap<>() {{
                put("data", data);
                put("info", info);
            }};

            Map<String, String> headers = new HashMap<>() {{
                put("Authorization", "Bearer s3cr3tb34r3r");
                put("User-Agent", "RML Processor");
            }};

            runTest(port, paths, headers, auth, "src/test/resources/wot/oauth_input.json", "$.connections[*]", "api");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Source> getJsonSources(Access access, String iterator) {
        try {
            List<Source> sources = new ArrayList<>();

            Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
            Object document = conf.jsonProvider().parse(access.getInputStream(), "utf-8");
            List<String> pathList = JsonPath.using(conf).parse(document).read(iterator);

            // TODO: replace
            pathList.forEach(path -> sources.add(new JSONSource(document, "", path)));

            return sources;
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
