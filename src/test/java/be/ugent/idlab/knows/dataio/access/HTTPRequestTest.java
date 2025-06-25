package be.ugent.idlab.knows.dataio.access;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.sun.net.httpserver.HttpServer;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Map;

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

    @Nested
    @Testcontainers
    class SolidTests {
        @Container
        GenericContainer<?> solid = new GenericContainer<>(DockerImageName.parse("solidproject/community-server:7"))
                .withEnv(Map.of(
                        "CSS_BASE_URL", "http://localhost:3000/",
                        "CSS_SEED_CONFIG", "/seed.json",
                        "CSS_CONFIG", "/config/playground.json",
                        "CSS_SHOW_STACK_TRACE", "true"))
                // add files for container setup
                .withCopyToContainer(MountableFile.forClasspathResource("/community_solid_server/config/"), "/config")
                .withCopyToContainer(MountableFile.forClasspathResource("/community_solid_server/playground-pod-seed.json"), "/seed.json")
                .withCopyToContainer(MountableFile.forClasspathResource("/community_solid_server/root-content/"), "/root-content/")
                // bypass Testcontainers' random port assignment
                .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(3000), new ExposedPort(3000))))
                // wait for internal server to start running
                .waitingFor(Wait.forLogMessage(".*Listening to server.*", 1));

        @Test
        public void solid_auth() throws JoseException, SQLException, IOException, ParserConfigurationException, TransformerException {
            // set up user1 folder
            solid.copyFileToContainer(MountableFile.forClasspathResource("/community_solid_server/user1_content/data.csv"), "/data/user1/dataio/data.csv");
            solid.copyFileToContainer(MountableFile.forClasspathResource("/community_solid_server/user1_content/data.acl"), "/data/user1/dataio/data.acl");

            String requestURL = "http://localhost:3000/user1/dataio/data.csv";
            String email = "user1@localhost";
            String password = "user1";
            String oidcIssuer = "http://localhost:3000/";
            String authWebId = "http://localhost:3000/user1/profile/card#me";

            HTTPRequestAccess access = new HTTPRequestAccess(requestURL, "GET");
            access.setAuthSolid(email, password, oidcIssuer, authWebId);

            String actual = new String(access.getInputStream().readAllBytes());

            try (FileInputStream fis = new FileInputStream("src/test/resources/community_solid_server/user1_content/data.csv")) {
                String expected = new String(fis.readAllBytes());
                assertEquals(expected, actual);
            }
        }
    }
}
