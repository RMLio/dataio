package be.ugent.idlab.knows.dataio.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Injects a known NULL value between two commas in CSV.
 * Inspired by <a href="https://stackoverflow.com/a/4588005">this answer on SO</a>
 */
public class CSVNullInjector extends InputStream {

    private static final byte[] NULL_VALUE = "DATAIO_INJECTED_NULL_VALUE".getBytes(Charset.defaultCharset());
    private final InputStream inputStream;
    private final Deque<Byte> backBuffer = new ArrayDeque<>();
    private final byte[] readBuffer = new byte[NULL_VALUE.length];
    private final char delimiter;
    private final char quoteCharacter;
    private boolean quoteMode = false;

    public CSVNullInjector(InputStream in, char delimiter, char quoteCharacter) {
        this.inputStream = in;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;
    }

    /**
     * Constructor with default delimiter and quote character for CSV
     *
     * @param in inputstream to read from
     */
    public CSVNullInjector(InputStream in) {
        this(in, ',', '"');
    }

    @Override
    public int read() throws IOException {
        if (!backBuffer.isEmpty()) {
            char value = (char) (int) backBuffer.pop();
            if (value == quoteCharacter) {
                quoteMode = !quoteMode;
            }
            return value;
        }

        int first = this.inputStream.read();

        if (first == quoteCharacter) {
            quoteMode = !quoteMode;
            return first;
        }

        if (first == delimiter) {
            if (quoteMode) { // if in quote mode, do not inject anything
                return first;
            }
            int second = this.inputStream.read();

            if (second != delimiter) {

                backBuffer.push((byte) (char) second);
            } else {
                peekAndReplace();
            }
        }
        return first;
    }

    private void peekAndReplace() throws IOException {
        int read = super.read(readBuffer, 0, NULL_VALUE.length);
        // fill in backbuffer
        for (int i = read - 1; i >= 0; i--) {
            backBuffer.push(readBuffer[i]);
        }
        backBuffer.push((byte) this.delimiter);

        for (int i = 0; i < NULL_VALUE.length; i++) {
            if (read != NULL_VALUE.length || readBuffer[i] != NULL_VALUE[i]) {
                for (int j = NULL_VALUE.length - 1; j >= 0; j--) {
                    backBuffer.push(NULL_VALUE[j]);
                }
                return;
            }
        }
    }
}
