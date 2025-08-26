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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <p>
 * Code adapted and modified from its original implementation in RMLMapper
 */
public class HTTPRequestAccess implements Access {

    private static final Logger log = LoggerFactory.getLogger(HTTPRequestAccess.class);
    private final Map<String, String> credentialsCache = new HashMap<>();
    private final Map<String, JSONObject> accessTokenCache = new HashMap<>();
    protected String requestURL;
    protected String methodName;
    protected String methodBody;
    protected Map<String, String> auth;
    protected Map<String, String> headers;
    protected HttpClient httpClient = HttpClient.newBuilder().build();
    private final EllipticCurveJsonWebKey jwk;

    /**
     * Constructor with method GET, empty authentication and empty headers
     *
     * @param requestURL URL to perform request to
     */
    public HTTPRequestAccess(String requestURL) {
        this(requestURL, "GET", null, Map.of(), Map.of());
    }

    /**
     * Main constructor.
     *
     * @param requestURL URL of the resource to consume
     * @param methodName name of the method to use during request
     * @param auth       authentication properties to use for the request
     * @param headers    headers to supply the request with
     */
    public HTTPRequestAccess(String requestURL,
                             String methodName,
                             String methodBody,
                             Map<String, String> headers,
                             Map<String, String> auth) {
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

    /**
     * Constructor with empty body, no headers and no auth
     *
     * @param url    URL to perform request to
     * @param method HTTP method of the request
     */
    public HTTPRequestAccess(String url, String method) {
        this(url, method, null, Map.of(), Map.of());
    }

    /**
     * Constructor with empty auth and no headers
     *
     * @param url    URL to perform request to
     * @param method HTTP method of the request
     * @param body   body to send with the request
     */
    public HTTPRequestAccess(String url, String method, String body) {
        this(url, method, body, Map.of(), Map.of());
    }

    @Override
    public InputStream getInputStream() throws IOException, SQLException, ParserConfigurationException, TransformerException {
        HttpRequest.Builder requestBuilder = HttpRequest
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
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
            HttpRequest request = requestBuilder.build();
            log.debug(request.uri().toString());
            HttpResponse<String> response = this.httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("Could not reach the server: " + response.body());
            }

            return new ByteArrayInputStream(response.body().getBytes(StandardCharsets.UTF_8));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set authentication key-values, added as headers to a HTTP request.
     * @param auth Authentication key-values, to be added to a HTTP request.
     */
    public void setAuth(Map<String, String> auth) {
        this.auth = auth;
    }

    /**
     * Sets the authentication map with credentials obtained through Solid authentication
     *
     * @param email      email for authentication
     * @param password   password for authentication
     * @param oidcIssuer issuer for authentication
     * @param authWebID  user's web identity
     * @throws JoseException when there's an error in generating the JWT
     */
    public void setAuthSolid(String email, String password, String oidcIssuer, String authWebID) throws JoseException {
        this.auth = this.getAuthHeadersSolid(email, password, oidcIssuer, authWebID, this.requestURL, this.methodName);
    }

    /**
     * It is impossible to tell by the source alone what datatypes this Access type will provide
     *
     * @return empty map
     */
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
     * Fetches the authentication headers for the specific request
     *
     * @param email      email of the user
     * @param password   password of the user
     * @param oidcIssuer identity issuer
     * @param webId      user's web identity
     * @param uri        URI of the resource to fetch
     * @param method     HTTP method to fetch the resource with
     * @return a Map of credentials required for authenticated access to Solid pods
     * @throws JoseException if the construction of JWT fails
     */
    public Map<String, String> getAuthHeadersSolid(String email, String password, String oidcIssuer, String webId, String uri, String method) throws JoseException {
        log.debug("Fetching dpop access token");
        // get D-PoP access token
        String dpop = getDpopAccessToken(email, password, oidcIssuer, webId);

        log.debug("Constructing jwt");
        // get jwt
        String dataJWT = generateJWT(uri, method);

        return Map.of(
                "Authorization", "DPoP " + dpop,
                "DPoP", dataJWT
        );
    }

    /**
     * Fetches the D-PoP access token
     *
     * @param email      email of the user
     * @param password   password of the user
     * @param oidcIssuer issuer of user's identity
     * @param webId      user's identity
     * @return the D-PoP access token, or null if unable to get the token
     */
    private String getDpopAccessToken(String email, String password, String oidcIssuer, String webId) {
        String clientCredentials;
        if (credentialsCache.containsKey(oidcIssuer)) {
            clientCredentials = credentialsCache.get(oidcIssuer);
        } else {
            clientCredentials =
                    fetchClientCredentials(email, password, oidcIssuer, webId);
        }

        String accessToken = null;
        boolean isValidToken = false;
        if (accessTokenCache.containsKey(webId)) {
            // check if the token is valid
            JSONObject token = new JSONObject(accessTokenCache.get(webId));
            long expiry = token.getLong("expires_on");
            long now = (new Date().getTime()) / 1000;
            if (now < expiry - 10) {
                accessToken = token.getString("access_token");
                isValidToken = true;
            }
        }

        if (!isValidToken) {
            accessToken = fetchAccessToken(oidcIssuer, webId, clientCredentials);
        }

        return accessToken;
    }

    /**
     * Fetches access token for the pod using client's credentials
     *
     * @param oidcIssuer        issuer of the identity
     * @param webId             web identification
     * @param clientCredentials credentials obtained for the user's pod
     * @return the access token
     */
    private String fetchAccessToken(String oidcIssuer, String webId, String clientCredentials) {
        try {
            JSONObject creds = new JSONObject(clientCredentials);
            String id = creds.getString("id");

            String secret = creds.getString("secret");

            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
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
                    .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
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

    /**
     * Generates the JWT to be used with the request
     *
     * @param url    URL to perform request to
     * @param method HTTP method to perform the request with
     * @return compact serialization of the generated JWT
     * @throws JoseException when there's an error creating the JWT
     */
    private String generateJWT(String url, String method) throws JoseException {
        JwtClaims claims = new JwtClaims();
        claims.setGeneratedJwtId();
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

    /**
     * Obtains client credentials for the user's pod
     *
     * @param email      email of the user
     * @param password   password of the user
     * @param oidcIssuer identity issuer
     * @param webId      web identity
     * @return String representation of the credentials
     */
    private String fetchClientCredentials(String email, String password, String oidcIssuer, String webId) {
        log.debug("Fetching client credentials");
        try {
            log.debug("Fetching account info");
            // fetch account info about the user
            HttpRequest request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
                    .uri(URI.create(oidcIssuer + ".account/"))
                    .GET()
                    .build();
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get account info: " + response.body());
            }

            // parse the response for the password login URL
            log.debug("Getting password login URL");
            JSONObject accountInfo = new JSONObject(response.body());
            String passwordURL = accountInfo.getJSONObject("controls")
                    .getJSONObject("password")
                    .getString("login");

            // perform login
            log.debug("Logging in");
            String message = new JSONObject(Map.of("email", email, "password", password)).toString();
            request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
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

            log.debug("Got authorization token");
            request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
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
            message = new JSONObject(Map.of("name", "my-token", "webId", webId)).toString();
            request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1) // CSS server seems to have problems with HTTP/2 upgrade
                    .uri(URI.create(credentialsURL))
                    .POST(HttpRequest.BodyPublishers.ofString(message, StandardCharsets.UTF_8))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Authorization", "CSS-Account-Token " + authToken)
                    .build();

            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException("Could not get OpenID Connect token info: " + response.body());
            }

            log.debug("Obtained credentials");

            String credentials = response.body();
            credentialsCache.put(webId, credentials);

            return credentials;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
