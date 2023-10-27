package be.ugent.idlab.knows.dataio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Injects a known NULL value between two commas in CSV.
 * CSVNullInjector will inject a specific null value (defined below in NULL_VALUE) between two delimiters and between a delimiter and a newline.
 * Inspired by <a href="https://stackoverflow.com/a/4588005">this answer on SO</a>, written with Java's native buffers.
 */
public class CSVNullInjector extends InputStream {
    public static final String NULL_VALUE = "DATAIO_NULL";
    private final CharBuffer nullBuffer;
    private final CharBuffer inputBuffer;
    private final InputStreamReader reader;
    private final char delimiter;
    private final char quoteCharacter;
    private boolean quoteMode = false;
    private boolean newLine = true;

    /**
     * Constructor for CSVNullInjector
     * Will initialise buffers and read the first amount of chars from the reader.
     *
     * @param reader         InputStreamReader containing the stream to consume
     * @param bufferSize     buffer size to pre-allocate for the inputBuffer and keep during reading
     * @param delimiter      used delimiter
     * @param quoteCharacter used quote character
     * @throws IOException when an I/O error occurs
     */
    public CSVNullInjector(InputStreamReader reader, int bufferSize, char delimiter, char quoteCharacter) throws IOException {
        this.nullBuffer = CharBuffer.allocate(NULL_VALUE.length());
        this.inputBuffer = CharBuffer.allocate(bufferSize);
        this.reader = reader;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;

        // initialise null buffer
        this.nullBuffer.put(NULL_VALUE);

        // initialise input buffer
        int count = this.reader.read(this.inputBuffer.array());
        this.inputBuffer.flip();
        if (count > 0) {
            this.inputBuffer.limit(count);
        }
    }

    /**
     * Constructor with default values for CSV
     *
     * @param reader     reader to consume
     * @param bufferSize size of the buffer to keep
     * @throws IOException when an I/O error occurs
     */
    public CSVNullInjector(InputStreamReader reader, int bufferSize) throws IOException {
        this(reader, bufferSize, ',', '"');
    }

    @Override
    public int read() throws IOException {
        ReadingResult rr = getNextByte();
        if (rr.valid()) {
            return rr.result();
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = off;
        while (i < len) {
            ReadingResult rr = getNextByte();
            if (!rr.valid()) {
                if (i == off) {
                    return -1; // return -1 to let the caller known no more data is available
                }
                break;
            }
            byte[] bytes = String.valueOf(rr.result()).getBytes();
            for (byte b1 : bytes) {
                b[i] = b1;
                i++;
            }
        }
        return i - off;
    }

    /**
     * Fetches the next character to be returned by the injector.
     * This character could come from either the nullBuffer or the inputBuffer, depending on the state of the injector
     *
     * @return the next character
     */
    private ReadingResult getNextByte() throws IOException {
        if (this.nullBuffer.hasRemaining()) {
            return new ReadingResult(true, this.nullBuffer.get());
        }

        if (this.noMoreInput()) {
            // nothing more in the inputBuffer
            return new ReadingResult(false, 'f');
        }

        char currentChar = this.inputBuffer.get();

        // specific case when we're on a new line and first character is the delimiter
        // -> there's a missing null value that must be injected
        if (this.newLine && currentChar == this.delimiter) {
            // move the inputBuffer back to original position
            this.inputBuffer.position(this.inputBuffer.position() - 1);
            this.nullBuffer.flip();
            this.newLine = false;
            return new ReadingResult(true, this.nullBuffer.get());
        }

        this.newLine = false;

        if (currentChar == this.quoteCharacter) {
            // toggle quote mode
            this.quoteMode = !this.quoteMode;
        }

        if (quoteMode) { // if in quote mode, immediately return
            return new ReadingResult(true, currentChar);
        }

        if (currentChar == '\n') { // encountered end of line, return
            this.newLine = true;
            return new ReadingResult(true, currentChar);
        }

        if (currentChar == this.delimiter) {
            // look for second delimiter
            if (this.noMoreInput()) {
                // last byte of the input is a delimiter, add one last null value
                this.nullBuffer.flip();
                return new ReadingResult(true, currentChar);
            }
            // not the last byte, check the next
            char b1 = this.inputBuffer.get(this.inputBuffer.position());
            if (b1 == this.delimiter || b1 == '\n') {
                // two delimiters or a newline => dangling delimiter, add a null value
                this.nullBuffer.flip();
                return new ReadingResult(true, currentChar); // return the original
            }
        }

        return new ReadingResult(true, currentChar);
    }

    private boolean noMoreInput() throws IOException {
        if (this.inputBuffer.hasRemaining()) {
            return false;
        }
        int count = this.reader.read(this.inputBuffer);
        if (count < 1) { // no bytes available
            return true;
        }

        this.inputBuffer.flip();
        this.inputBuffer.limit(count);

        return false;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    /**
     * A convenience method for getting an InputStreamReader
     *
     * @return an InputStreamReader that consumes this null injector
     */
    public InputStreamReader reader() {
        return new InputStreamReader(this);
    }

    /**
     * Record to communicate the result of the byte read and its success
     *
     * @param valid  true if the reading of the byte was successful and the result byte is usable, false otherwise
     * @param result the character produced by the read. If valid == false, its value does not matter
     */
    private record ReadingResult(boolean valid, char result) {
    }
}
