package be.ugent.idlab.knows.dataio.utils;

import com.drew.lang.annotations.NotNull;
import io.reactivex.rxjava3.annotations.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Injects a known NULL value between two commas in CSV.
 * Inspired by <a href="https://stackoverflow.com/a/4588005">this answer on SO</a>
 */
public class CSVNullInjector extends InputStream {

    private static final byte[] NULL_VALUE = "DATAIO_INJECTED_NULL_VALUE".getBytes(StandardCharsets.UTF_8);
    private final InputStream inputStream;
    private final Deque<Byte> backBuffer = new ArrayDeque<>();
    private final char delimiter;
    private final char quoteCharacter;
    private boolean quoteMode = false;
    private boolean firstRead = true;

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

        if (first == '\n') {
            firstRead = true;
            return first;
        }

        if (first == quoteCharacter) {
            quoteMode = !quoteMode;
            return first;
        }

        if (first == delimiter) {
            if (quoteMode) { // if in quote mode, do not inject anything
                return first;
            }

            if (firstRead) {
                backBuffer.push((byte) this.delimiter);
                addNullToBackBuffer();
                firstRead = false;
                return (int) backBuffer.pop();
            } else {
                int second = this.inputStream.read();

                if (second != delimiter) {
                    backBuffer.push((byte) (char) second);
                } else {
                    backBuffer.push((byte) this.delimiter);
                    addNullToBackBuffer();
                }
            }
        }
        firstRead = false;

        return first;
    }

    private void addNullToBackBuffer() {
        for (int i = NULL_VALUE.length - 1; i >= 0; i--) {
            backBuffer.push(NULL_VALUE[i]);
        }
    }
}
