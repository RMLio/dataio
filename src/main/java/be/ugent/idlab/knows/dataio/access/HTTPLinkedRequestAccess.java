package be.ugent.idlab.knows.dataio.access;

import net.snowflake.client.jdbc.internal.amazonaws.HttpMethod;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HTTPLinkedRequestAccess extends HTTPRequestAccess {
    private String linkingURI, linkRelation;

    public HTTPLinkedRequestAccess(String linkingURI, String linkRelation, Map<String, String> auth) {
        super(null); // for now, will be resolved to correct value by getInputStream
        this.linkingURI = linkingURI;
        this.linkRelation = linkRelation;
        super.auth = auth;
    }

    @Override
    public InputStream getInputStream() throws IOException, SQLException, ParserConfigurationException, TransformerException {
        // resolve the link by performing a HEAD request
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(this.linkingURI))
                .method(HttpMethod.HEAD.name(), HttpRequest.BodyPublishers.noBody());

        for (Map.Entry<String, String> entry : super.auth.entrySet()) {
            requestBuilder.setHeader(entry.getKey(), entry.getValue());
        }

        String absoluteURI = null;
        try {
            HttpResponse<String> response = this.httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

            // grab the link relation
            List<String> links = response.headers().map().get("link");


            for (String link : links) {
                if (link.contains(String.format("rel=\"%s\"", linkRelation))) {
                    absoluteURI = link.substring(link.indexOf('<') + 1, link.indexOf('>'));
                    break;
                }
            }

            if (absoluteURI == null) {
                throw new RuntimeException("Could not get the linked absolute URI for link relation " + linkRelation + " and linking absolute URI " + linkingURI);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.requestURL = absoluteURI;

        return super.getInputStream();
    }
}
