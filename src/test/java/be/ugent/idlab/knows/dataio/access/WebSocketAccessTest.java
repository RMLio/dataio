package be.ugent.idlab.knows.dataio.access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WebSocketAccessTest {

    private static class TestWsServer extends WebSocketServer {

        public TestWsServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            conn.send("hello");
            conn.send("world");
            conn.close(1000);
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
        }

        @Override
        public void onStart() {
        }
    }

    private TestWsServer server;
    private int port;

    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        try (ServerSocket ss = new ServerSocket(0)) {
            this.port = ss.getLocalPort();
        }

        server = new TestWsServer(new InetSocketAddress("127.0.0.1", port));
        server.start();
        // give server a moment to bind
        Thread.sleep(150);
    }

    @AfterEach
    public void stopServer() throws InterruptedException {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testCollectsMessagesIntoStream() throws Exception {
        String uri = "ws://127.0.0.1:" + port;
        WebSocketAccess access = new WebSocketAccess(uri, java.util.Map.of(), Duration.ofSeconds(2));

        try (InputStream in = access.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String all = reader.lines().reduce("", (a, b) -> a + (a.isEmpty() ? "" : "\n") + b);
            assertTrue(all.contains("hello"));
            assertTrue(all.contains("world"));
        }
    }
}


