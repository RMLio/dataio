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
    private final ByteBuffer nullBuffer;
    private final ByteBuffer inputBuffer;
    private final InputStream inputStream;
    private final byte delimiter;
    private final byte quoteCharacter;
    private boolean quoteMode = false;
    private boolean newLine = true;

    public CSVNullInjector(InputStream inputStream, int bufferSize, byte delimiter, byte quoteCharacter) throws IOException {
        this.nullBuffer = ByteBuffer.allocate(NULL_VALUE.length());
        this.inputBuffer = ByteBuffer.allocate(bufferSize);
        this.inputStream = inputStream;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;

        // initialise null buffer
        this.nullBuffer.put(NULL_VALUE.getBytes());

        // initialise input buffer
        int count = this.inputStream.read(this.inputBuffer.array());
        if (count > 0) {
            this.inputBuffer.limit(count);
        }
    }

    public CSVNullInjector(InputStream inputStream, int bufferSize) throws IOException {
        this(inputStream, bufferSize, (byte) ',', (byte) '"');
    }

    @Override
    public int read() throws IOException {
        ReadingResult rr = getNextByte();
        if (rr.valid) {
            return rr.result & 0xFF; // sign extend the byte
        }
        return -1; // nothing could be read
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = off;
        for (; i < len; i++) {
            ReadingResult rr = getNextByte();
            if (!rr.valid()) {
                if (i == off) {
                    return -1; // return -1 to let the caller known no more data is available
                }
                break;
            }
            b[i] = rr.result();
        }
        return i - off;
    }

    /**
     * Fetches next byte to be returned in by the injector.
     * This byte could come from either the nullBuffer or the inputBuffer, depending on the state of the injector
     *
     * @return the next byte
     */
    private ReadingResult getNextByte() throws IOException {
        if (this.nullBuffer.hasRemaining()) {
            return new ReadingResult(true, this.nullBuffer.get());
        }

        if (!this.ensureInput()) {
            // nothing more in the inputBuffer
            return new ReadingResult(false, (byte) -1);
        }

        byte currentByte = this.inputBuffer.get();

        // specific case when we're on a new line and first character is the delimiter
        // -> there's a missing null value that must be injected
        if (this.newLine && currentByte == this.delimiter) {
            this.inputBuffer.position(this.inputBuffer.position() - 1);
            this.nullBuffer.flip();
            this.newLine = false;
            return new ReadingResult(true, this.nullBuffer.get());
        }

        this.newLine = false;

        if (currentByte == this.quoteCharacter) {
            // toggle quote mode
            this.quoteMode = !this.quoteMode;
        }

        if (currentByte == '\n') {
            this.newLine = true;
            return new ReadingResult(true, currentByte);
        }

        if (quoteMode) { // if in quote mode, immediately return
            return new ReadingResult(true, currentByte);
        }

        if (currentByte == this.delimiter) {
            // look for second delimiter
            if (!this.ensureInput()) {
                // last byte of the input is a delimiter, add one last null value
                this.nullBuffer.flip();
                return new ReadingResult(true, currentByte);
            }
            // not the last byte, check the next
            byte b1 = this.inputBuffer.get(this.inputBuffer.position());
            if (b1 == this.delimiter || b1 == '\n') {
                // two delimiters or a newline => dangling delimiter, add a null value
                this.nullBuffer.flip();
                return new ReadingResult(true, currentByte); // return the original
            }
        }

        return new ReadingResult(true, currentByte);
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
        this.inputBuffer.limit(count);

        return true;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    /**
     * Record to communicate the result of the byte read and it's success
     * @param valid true if the reading of the byte was successful and the result byte is usable, false otherwise
     * @param result the result of reading
     */
    private record ReadingResult(boolean valid, byte result) {
    }
}
