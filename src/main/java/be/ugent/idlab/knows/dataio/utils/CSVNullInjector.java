package be.ugent.idlab.knows.dataio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Injects a known NULL value between two commas in CSV.
 * Inspired by <a href="https://stackoverflow.com/a/4588005">this answer on SO</a>, written with Java's native buffers.
 */
public class CSVNullInjector extends InputStream {
    public static final String NULL_VALUE = "DATAIO_NULL";
    private static final int BUFFER_SIZE = 1024 * 128;
    private final ByteBuffer nullBuffer;
    private final ByteBuffer inputBuffer;
    private final InputStream inputStream;
    private final char delimiter;
    private final char quoteCharacter;
    private boolean quoteMode = false;
    private boolean newLine = true;

    public CSVNullInjector(InputStream inputStream, int bufferSize, char delimiter, char quoteCharacter) throws IOException {
        this.nullBuffer = ByteBuffer.allocate(NULL_VALUE.length());
        this.inputBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.inputStream = inputStream;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;

        // initialise null buffer
        this.nullBuffer.put(NULL_VALUE.getBytes());

        // initialise input buffer
        byte[] bytes = this.inputStream.readNBytes(BUFFER_SIZE);
        this.inputBuffer.put(bytes);
        this.inputBuffer.flip();
    }

    public CSVNullInjector(InputStream inputStream, int bufferSize) throws IOException {
        this(inputStream, bufferSize, ',', '"');
    }

    @Override
    public int read() throws IOException {
        return getNextByte() & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = off;
        for (; i < len; i++) {
            byte b1 = getNextByte();
            if (b1 == -1) {
                if (i == off) {
                    return -1; // return -1 to let the caller known no more data is available
                }
                break;
            }
            b[i] = b1;
        }
        return i - off;
    }

    /**
     * Fetches next byte to be returned in by the injector.
     * This byte could come from either the nullBuffer or the inputBuffer, depending on the state of the injector
     *
     * @return the next byte
     */
    private byte getNextByte() throws IOException {
        if (this.nullBuffer.hasRemaining()) {
            return this.nullBuffer.get();
        }

        if (!this.ensureInput()) {
            // nothing more in the inputBuffer
            return -1;
        }

        byte b = this.inputBuffer.get();

        // specific case when we're on a new line and first character is the delimiter
        // -> there's a missing null value that must be injected
        if (this.newLine && b == this.delimiter) {
            this.inputBuffer.position(this.inputBuffer.position() - 1);
            this.nullBuffer.flip();
            this.newLine = false;
            return this.nullBuffer.get();
        }

        this.newLine = false;

        if (b == this.quoteCharacter) {
            // toggle quote mode
            this.quoteMode = !this.quoteMode;
        }

        if (b == '\n') {
            this.newLine = true;
            return b;
        }

        if (quoteMode) { // if in quote mode, immediately return
            return b;
        }

        if (b == this.delimiter) {
            // look for second delimiter
            if (!this.ensureInput()) {
                // last byte of the input is a delimiter, add one last null value
                this.nullBuffer.flip();
                return b;
            }
            // not the last byte, check the next
            byte b1 = this.inputBuffer.get(this.inputBuffer.position());
            if (b1 == this.delimiter) {
                // two delimiters, add a null value
                this.nullBuffer.flip();
                return b; // return the original
            }
        }

        return b;
    }

    private boolean ensureInput() throws IOException {
        if (this.inputBuffer.hasRemaining()) {
            return true;
        }

        int count = this.inputStream.read(this.inputBuffer.array());
        if (count < 1) { // no bytes available
            return false;
        }

        this.inputBuffer.flip();

        return true;
    }
}
