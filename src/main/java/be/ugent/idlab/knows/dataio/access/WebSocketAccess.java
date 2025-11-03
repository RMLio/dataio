package be.ugent.idlab.knows.dataio.access;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access implementation for consuming data from a WebSocket endpoint.
 * Messages received during the configured window are concatenated into a single InputStream.
 */
public class WebSocketAccess implements Access {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAccess.class);
    @Serial
    private static final long serialVersionUID = -238764532187654321L;

    private final String uri;
    private final Map<String, String> headers;
    private final Duration receiveWindow;

    /**
     * Construct a WebSocket access with default 2 second receive window and no headers.
     * @param uri WebSocket URI (ws:// or wss://)
     */
    public WebSocketAccess(String uri) {
        this(uri, Map.of(), Duration.ofSeconds(2));
    }

    /**
     * Construct a WebSocket access.
     * @param uri WebSocket URI (ws:// or wss://)
     * @param headers Headers to include in the opening handshake
     * @param receiveWindow Time window to collect messages before returning the stream
     */
    public WebSocketAccess(String uri, Map<String, String> headers, Duration receiveWindow) {
        this.uri = uri;
        this.headers = headers;
        this.receiveWindow = receiveWindow;
    }

    @Override
    public InputStream getInputStream() throws IOException, SQLException, ParserConfigurationException, TransformerException {
        HttpClient client = HttpClient.newHttpClient();

        StringBuilder buffer = new StringBuilder();
        CountDownLatch closeLatch = new CountDownLatch(1);

        WebSocket.Builder builder = client.newWebSocketBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        WebSocket ws = builder.buildAsync(URI.create(this.uri), new WebSocket.Listener() {

            @Override
            public void onOpen(WebSocket webSocket) {
                log.debug("WebSocket opened: {}", uri);
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                buffer.append(data);
                if (last) {
                    buffer.append('\n');
                }
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
                // Treat binary as UTF-8 text for uniformity
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                buffer.append(new String(bytes, StandardCharsets.UTF_8));
                if (last) {
                    buffer.append('\n');
                }
                return WebSocket.Listener.super.onBinary(webSocket, data, last);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                log.debug("WebSocket closed: {} code={} reason={}", uri, statusCode, reason);
                closeLatch.countDown();
                return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                log.warn("WebSocket error on {}: {}", uri, error.getMessage());
                closeLatch.countDown();
            }
        }).join();

        // Wait either for the server to close the connection, or for the receive window to elapse
        try {
            boolean closed = closeLatch.await(this.receiveWindow.toMillis(), TimeUnit.MILLISECONDS);
            if (!closed) {
                // If still open after the window, attempt to close politely
                ws.sendClose(WebSocket.NORMAL_CLOSURE, "done");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }

        byte[] bytes = buffer.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
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
        return this.uri;
    }
}


