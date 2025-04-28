package be.ugent.idlab.knows.dataio.access;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.sql.SQLException;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPRequestTest {

    @Test
    public void basicSmokeTest() throws IOException, SQLException, ParserConfigurationException, TransformerException {
        String expected = readFileToString(new File("src/test/resources/csv/0001.csv"), Charset.defaultCharset());
        var arr = expected.getBytes();
        // prepare a server
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/test", httpExchange -> {
            httpExchange.sendResponseHeaders(200, arr.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(arr);
            os.close();
        });
        server.setExecutor(null);

        server.start();

        HTTPRequestAccess access = new HTTPRequestAccess(
                String.format("http://%s:%d/test", server.getAddress().getHostString(), server.getAddress().getPort()));
        String actual = new String(access.getInputStream().readAllBytes(), Charset.defaultCharset());
        try {
            assertEquals(expected, actual);
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void foo() {
        
    }
}
