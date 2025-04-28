package be.ugent.idlab.knows.dataio.access;

import net.snowflake.client.jdbc.internal.apache.http.HttpStatus;
import org.jose4j.jwk.EcJwkGenerator;
import org.jose4j.jwk.EllipticCurveJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.EllipticCurves;
import org.jose4j.lang.JoseException;
import org.json.JSONObject;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements an HTTP request to support <a href="https://rml.io/specs/access/httprequest/">HTTP REQUEST ACCESS</a>
 */
public class HTTPRequestAccess implements Access {

    //    private static final Map<String, String> referenceFormulationToContentType = new HashMap<>() {{
//        put(NAMESPACES.RML + "CSV", "text/plain");
//        put(NAMESPACES.RML + "JSONPath", "application/json");
//        put(NAMESPACES.RML + "XPath", "application/xml");
//        put(NAMESPACES.RML + "XPathReferenceFormulation", "application/xml");
//        put(NAMESPACES.FORMATS + "SPARQL_Results_CSV", "text/plain");
//        put(NAMESPACES.FORMATS + "SPARQL_Results_TSV", "text/plain");
//        put(NAMESPACES.FORMATS + "SPARQL_Results_JSON", "application/json");
//        put(NAMESPACES.FORMATS + "SPARQL_Results_XML", "application/xml");
//    }};
    protected String requestURL;
    protected String methodName;
    protected String methodBody;
    protected Map<String, String> auth;
    protected Map<String, String> headers;
    protected HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    private Map<String, String> credentialsCache = new HashMap<>();
    private Map<String, JSONObject> accessTokenCache = new HashMap<>();
    private EllipticCurveJsonWebKey jwk;

    private HTTPRequestAccess() {
    }

    /**
     * Default constructor with method GET, empty authentication and empty headers, operating fully within memory
     */
    public HTTPRequestAccess(String requestURL) {
        this(requestURL, "GET", null, Map.of(), Map.of());
    }

    /**
     * @param requestURL URL of the resource to consume
     * @param methodName name of the method to use during request
     * @param auth       authentication properties to use for the request
     * @param headers    headers to supply the request with
     */
    public HTTPRequestAccess(String requestURL,
                             String methodName,
                             String methodBody,
                             Map<String, String> auth,
                             Map<String, String> headers) {
        this.requestURL = requestURL;
        this.methodName = methodName;
        this.auth = auth;
        this.headers = headers;
        this.methodBody = methodBody;
        try {
            this.jwk = EcJwkGenerator.generateJwk(EllipticCurves.P256);
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
    }

    public static HTTPRequestAccessBuilder builder() {
        return new HTTPRequestAccessBuilder();
    }

    @Override
    public InputStream getInputStream() throws IOException, SQLException, ParserConfigurationException, TransformerException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(this.requestURL));
        if (this.methodBody != null) {
            requestBuilder = requestBuilder.method(this.methodName, HttpRequest.BodyPublishers.ofString(this.methodBody));
        } else {
            requestBuilder = requestBuilder.method(this.methodName, HttpRequest.BodyPublishers.noBody());
        }

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            requestBuilder.setHeader(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, String> entry : this.auth.entrySet()) {
            requestBuilder.setHeader(entry.getKey(), entry.getValue());
        }

        try {
            HttpResponse<String> response = this.httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("Could not reach the server: " + response.body());
            }

            return new ByteArrayInputStream(response.body().getBytes(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getDataTypes() {
        return Map.of();
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public String getAccessPath() {
        return "";
    }

    /**
     * Generates Authorization headers for Community Solid Server
     *
     * @return
     */
    public Map<String, String> getAuthHeaders(String oidcIssuer, String webId, String email, String password, String uri, String method) throws JoseException {
        // get D-PoP access token
        String dpop = getDpopAccessToken(oidcIssuer, webId, email, password);

        // get jwt
        String dataJWT = generateJWT(uri, method);

        return Map.of(
                "Authorization", "DPoP " + dpop,
                "DPoP", dataJWT
        );
    }

    private String getDpopAccessToken(String oidcIssuer, String webId, String email, String password) {
        String clientCredentials;
        if (credentialsCache.containsKey(oidcIssuer)) {
            clientCredentials = credentialsCache.get(oidcIssuer);
        } else {
            clientCredentials = fetchClientCredentials(oidcIssuer, webId, email, password);
        }

        String accessToken = null;
        boolean isValidToken = false;
        if (accessTokenCache.containsKey(webId)) {
            // check if the token is valid
            JSONObject token = new JSONObject(accessTokenCache.get(webId));
            long expiry = token.getLong("expires_on");
            long now = ((new Date().getTime()) / 1000) + 10;
            if (now < expiry) {
                accessToken = token.getString("access_token");
                isValidToken = true;
            }
        }

        if (!isValidToken) {
            accessToken = fetchAccessToken(oidcIssuer, webId, clientCredentials);
        }

        return accessToken;
    }

    private String fetchAccessToken(String oidcIssuer, String webId, String clientCredentials) {
        try{
            JSONObject creds = new JSONObject(clientCredentials);
            String id = creds.getString("id");

            String secret = creds.getString("secret");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(oidcIssuer + ".well-known/openid-configuration"))
                    .GET()
                    .build();

            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get OpenID Connect info: " + response.body());
            }

            JSONObject oidcInfo = new JSONObject(response.body());

            String tokenEndpoint = oidcInfo.getString("token_endpoint");
            String dpopJWT = generateJWT(tokenEndpoint, "POST");

            // get an access token by POSTing to token endpoint with user credentials
            String concatCredentials = id + ':' + secret;
            String base64ClientCredentials = Base64.getEncoder().encodeToString(concatCredentials.getBytes(StandardCharsets.UTF_8));

            request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenEndpoint))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope=webid", StandardCharsets.UTF_8))
                    .setHeader("Authorization", "Basic " + base64ClientCredentials)
                    .setHeader("Content-Type", "application/x-www-form-urlencoded")
                    .setHeader("DPoP", dpopJWT)
                    .build();

            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get OpenID Connect info: " + response.body());
            }

            long start = ((new Date().getTime()) / 1000);
            JSONObject accessTokenObj = new JSONObject(response.body());
            accessTokenObj.put("expires_on", start + accessTokenObj.getInt("expires_in"));
            accessTokenCache.put(webId, accessTokenObj);

            return accessTokenObj.getString("access_token");
        } catch (IOException | InterruptedException | JoseException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateJWT(String url, String method) throws JoseException {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("htm", method);
        claims.setClaim("htu", url);
        claims.setIssuedAtToNow();

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(jwk.getPrivateKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256);
        jws.setHeader("typ", "dpop+jwt");
        jws.setJwkHeader(jwk);
        jws.sign();

        return jws.getCompactSerialization();
    }

    private String fetchClientCredentials(String oidcIssuer, String webId, String email, String password) {
        try {
            // fetch account info about the user
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(oidcIssuer + ".account/"))
                    .GET()
                    .build();
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get account info: " + response.body());
            }

            // parse the response for the password login URL
            JSONObject accountInfo = new JSONObject(response.body());
            String passwordURL = accountInfo.getJSONObject("controls")
                    .getJSONObject("password")
                    .getString("login");

            // perform login
            String message = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(passwordURL))
                    .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                    .setHeader("Content-Type", "application/json")
                    .build();
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not log in: " + response.body());
            }

            JSONObject loginInfo = new JSONObject(response.body());
            String authToken = loginInfo.getString("authorization");

            request = HttpRequest.newBuilder()
                    .uri(URI.create(oidcIssuer + ".account/"))
                    .GET()
                    .setHeader("Authorization", "CSS-Account-Token " + authToken)
                    .build();
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get account info even after logging in: " + response.body());
            }

            JSONObject authAccountInfo = new JSONObject(response.body());
            String credentialsURL = authAccountInfo.getJSONObject("controls")
                    .getJSONObject("account")
                    .getString("clientCredentials");

            // finally, get the useful credentials by POSTing to the credentials URL
            message = String.format("\"name\": \"my-token\", \"webId\": \"%s\"}", webId);
            request = HttpRequest.newBuilder()
                    .uri(URI.create(credentialsURL))
                    .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Authorization", "CSS-Account-Token " + authToken)
                    .build();

            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get OpenID Connect token info: " + response.body());
            }

            String credentials = response.body();
            credentialsCache.put(webId, credentials);

            return credentials;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A utility builder to make constructing the Access easier.
     */
    public static class HTTPRequestAccessBuilder {
        private final HTTPRequestAccess access;

        public HTTPRequestAccessBuilder() {
            this.access = new HTTPRequestAccess();
        }

        public HTTPRequestAccessBuilder withURL(String url) {
            this.access.requestURL = url;
            return this;
        }

        public HTTPRequestAccessBuilder withMethod(String method) {
            this.access.methodName = method;
            return this;
        }

        public HTTPRequestAccessBuilder withHeader(String key, String value) {
            this.access.headers.put(key, value);
            return this;
        }

        /**
         * Fully replaces the headers map with the argument
         *
         * @param headers
         * @return this
         */
        public HTTPRequestAccessBuilder withHeaders(Map<String, String> headers) {
            this.access.headers = headers;
            return this;
        }

        public HTTPRequestAccessBuilder withAuth(String key, String value) {
            this.access.auth.put(key, value);
            return this;
        }

        /**
         * Fully replaces the auth map with the argument
         *
         * @param auth
         * @return this
         */
        public HTTPRequestAccessBuilder withAuth(Map<String, String> auth) {
            this.access.auth = auth;
            return this;
        }

        public HTTPRequestAccess build() {
            return this.access;
        }
    }
}
