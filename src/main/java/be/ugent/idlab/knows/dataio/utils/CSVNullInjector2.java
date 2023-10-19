package be.ugent.idlab.knows.dataio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class CSVNullInjector2 extends CSVNullInjector {
    private static final byte[] NULL_VALUE = "DATAIO_INJECTED_NULL_VALUE".getBytes();
    private static final int BUFFER_SIZE = 1024 * 16; // 16 KiB
    private final InputStream inputStream;
    private final char delimiter;
    private final char quoteCharacter;
    private final Buffer buffer;
    private final Deque<Byte> backBuffer = new ArrayDeque<>(NULL_VALUE.length);
    private boolean quoteMode;
    private boolean newLine = true;

    public CSVNullInjector2(InputStream inputStream, char delimiter, char quoteCharacter) {
        super(inputStream, delimiter, quoteCharacter);
        this.inputStream = inputStream;
        this.delimiter = delimiter;
        this.quoteCharacter = quoteCharacter;

        this.buffer = new Buffer(inputStream, BUFFER_SIZE); // 8 KiB
    }

    public CSVNullInjector2(InputStream inputStream) {
        this(inputStream, ',', '"');
    }

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = off;
        for (; i < len; i++) {
            if (!backBuffer.isEmpty()) {
                b[i] = backBuffer.pop();
            } else {
                byte b1 = buffer.pop();
                if (b1 == -1) {
                    // nothing further could be read, abort
                    if (i == off) { // if no data was read at all
                        return -1;
                    }
                    break;
                }

                if (newLine && b1 == this.delimiter) {
                    backBuffer.push((byte) this.delimiter);
                    addNullToBackBuffer();
                    b[i] = backBuffer.pop();
                } else {
                    b[i] = b1;

                    // check for a new line
                    if (b1 == '\n') {
                        newLine = true;
                    } else if (newLine) {
                        newLine = false;
                    }

                    if (b1 == quoteCharacter) {
                        quoteMode = !quoteMode;
                    } else if (b1 == delimiter) {
                        if (!quoteMode) {
                            if (newLine) {
                                backBuffer.push((byte) this.delimiter);
                                addNullToBackBuffer();
                                newLine = false;
                                b[i] = backBuffer.pop();
                            } else {
                                byte second = buffer.peek();
                                if (second == this.delimiter) {
                                    addNullToBackBuffer();
                                }
                            }
                        }
                    }
                }
            }
        }

        return i - off;
    }

    private void addNullToBackBuffer() {
        for (int i = NULL_VALUE.length - 1; i >= 0; i--) {
            backBuffer.push(NULL_VALUE[i]);
        }
    }

    /**
     * Class serving as a buffer of bytes
     */
    private static class Buffer {
        InputStream inputStream;
        byte[] buffer; // buffer to hold the data
        int cursor; // cursor in the buffer
        int actualSize; // end of the buffer

        public Buffer(InputStream inputStream, int bufferSize) {
            this.inputStream = inputStream;
            this.buffer = new byte[bufferSize];
            this.cursor = 0;
        }

        /**
         * Pop a byte off the buffer when requested
         *
         * @return -1 if nothing more is available, otherwise the byte from the top of the buffer
         */
        public byte pop() throws IOException {
            if (this.cursor >= this.actualSize) { // if end of current buffer
                if (!loadNewData()) {
                    return -1;
                }
            }

            byte out = this.buffer[this.cursor];
            cursor++;
            return out;
        }

        public byte peek() throws IOException {
            if (this.cursor >= this.actualSize) {
                if (!loadNewData()) {
                    return -1;
                }
            }

            return this.buffer[this.cursor];
        }

        private boolean loadNewData() throws IOException {
            this.actualSize = this.inputStream.read(this.buffer, 0, buffer.length);
            if (this.actualSize == -1) { // if nothing further was read from the input stream
                return false;
            }

            this.cursor = 0;
            return true;
        }
    }
}
