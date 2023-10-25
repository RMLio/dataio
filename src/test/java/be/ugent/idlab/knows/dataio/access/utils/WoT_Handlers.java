package be.ugent.idlab.knows.dataio.access.utils;

import be.ugent.idlab.knows.dataio.utils.Utils;
import com.jayway.jsonpath.Configuration;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class where all the HttpHandlers used are collected
 */
public class WoT_Handlers {

    /**
     * Prepares and starts a fresh instance of HttpServer
     *
     * @param handlerMap a map with API endpoints and handlers to handle the requests
     * @return a fresh HttpServer instance, with paths set up according to handlerMap.
     * @throws IOException when passed port is unavailable
     */
    public static HttpServer getServer(int port, Map<String, HttpHandler> handlerMap) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            server.createContext(entry.getKey(), entry.getValue());
        }

        server.setExecutor(null); // default executor
        server.start();

        return server;
    }

    public static class TrashCansFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "couldn't load trashcan JSON file";
            try {
                response = Files.readString(Path.of("src/test/resources/wot/iot-sensors.json"), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            List<String> contentType = new ArrayList<>();
            contentType.add("application/json");
            List<String> key = t.getRequestHeaders().get("apikey");

            // Check API key
            try {
                if (key.get(0).equals("123456789")) {
                    t.getResponseHeaders().put("Content-Type", contentType);
                    t.sendResponseHeaders(200, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                // Wrong API key
                else {
                    t.sendResponseHeaders(401, response.length());
                }
            }
            // No API key provided
            catch (IndexOutOfBoundsException e) {
                t.sendResponseHeaders(401, response.length());
            }

        }
    }

    public static class IRailStationHandler1 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            // Redirect HTTP 302
            List<String> newLocation = new ArrayList<>();
            String response = "Redirected to /stations";
            newLocation.add("http://" + t.getLocalAddress().getHostName() + ":" + t.getLocalAddress().getPort() + "/stations");
            t.getResponseHeaders().put("Location", newLocation);
            t.sendResponseHeaders(302, response.length());
        }
    }

    public static class IRailStationHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "couldn't load iRail stations JSON file";
            try {
                response = Files.readString(Path.of("src/test/resources/wot/stations.json"), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            List<String> contentType = new ArrayList<>();
            contentType.add("application/json");

            // Return stations if not redirected
            t.getResponseHeaders().put("Content-Type", contentType);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class BearerAuthenticationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            this.validateRequestHeaders(exchange.getRequestHeaders());
            String response = "Couldn't load JSON file";
            try {
                String filePath = "src/test/resources/wot/bearer_security_input.json";
                response = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            List<String> contentType = new ArrayList<>();
            contentType.add("application/json");

            // Return response if not redirected
            exchange.getResponseHeaders().put("Content-Type", contentType);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void validateRequestHeaders(Headers requestHeaders) {
            // Assert that request header is not empty
            assert !requestHeaders.isEmpty();
            List<String> authorizationHeaders = requestHeaders.get("Authorization");
            // Assert the Authorization-header is present in the request
            assert !authorizationHeaders.isEmpty();
            String authorizationHeader = authorizationHeaders.get(0);
            // Assert that the bearer value is correct
            assert authorizationHeader.equals("Bearer s3cr3tb34r3r");
        }
    }

    public static class OAuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            this.validateRequestHeaders(exchange.getRequestHeaders());
            String response = "Couldn't load JSON file";
            try {
                String filePath = "src/test/resources/wot/oauth_input.json";
                response = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            List<String> contentType = new ArrayList<>();
            contentType.add("application/json");

            // Return response if not redirected
            exchange.getResponseHeaders().put("Content-Type", contentType);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void validateRequestHeaders(Headers requestHeaders) {
            // Assert that request header is not empty
            assert !requestHeaders.isEmpty();
            List<String> authorizationHeaders = requestHeaders.get("Authorization");
            // Assert the Authorization-header is present in the request
            assert !authorizationHeaders.isEmpty();
            String authorizationHeader = authorizationHeaders.get(0);
            // Assert that the bearer value is correct
            assert authorizationHeader.equals("Bearer s3cr3tb34r3r");
        }
    }

    public static class OAuthRefreshApi implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Couldn't load JSON file";
            if (this.validateRequestHeaders(exchange.getRequestHeaders())) {
                try {
                    String filePath = "src/test/resources/wot/oauth_input.json";
                    response = Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                List<String> contentType = new ArrayList<>();
                contentType.add("application/json");

                // Return response if not redirected
                exchange.getResponseHeaders().put("Content-Type", contentType);
                exchange.sendResponseHeaders(200, response.length());

            } else {
                exchange.sendResponseHeaders(401, response.length());
            }
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private boolean validateRequestHeaders(Headers requestHeaders) {
            // Assert that request header is not empty
            if (requestHeaders.isEmpty()) return false;
            List<String> authorizationHeaders = requestHeaders.get("Authorization");
            // Assert the Authorization-header is present in the request
            if (authorizationHeaders.isEmpty()) return false;
            String authorizationHeader = authorizationHeaders.get(0);
            // Assert that the bearer value is correct
            return authorizationHeader.equals("Bearer newToken");
        }
    }

    public static class OAuthRefreshAuth implements HttpHandler {
        private final Logger logger = LoggerFactory.getLogger(OAuthRefreshAuth.class);

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            this.validateBody(exchange.getRequestBody());
            String response = "{\"access_token\": \"newToken\"}";
            List<String> contentType = new ArrayList<>();
            contentType.add("application/json");

            // Return response if not redirected
            exchange.getResponseHeaders().put("Content-Type", contentType);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void validateBody(InputStream body) {
            logger.debug("trying to validate refresh request");
            HashMap<String, String> jsonResponse = (HashMap<String, String>) Configuration.defaultConfiguration().jsonProvider().parse(body, "utf-8");
            assert jsonResponse.containsKey("refresh");
            assert jsonResponse.get("refresh").equals("xur2saef4s");

            assert jsonResponse.containsKey("client_id");
            assert jsonResponse.get("client_id").equals("testID");

            assert jsonResponse.containsKey("client_secret");
            assert jsonResponse.get("client_secret").equals("testSecret");
            logger.debug("successfully validated refresh request");
        }
    }
}
