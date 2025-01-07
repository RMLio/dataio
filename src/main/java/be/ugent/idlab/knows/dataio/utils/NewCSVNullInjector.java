package be.ugent.idlab.knows.dataio.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NewCSVNullInjector extends InputStream {

    public final static String NULL_VALUE = "DATAIO_NULL";
    public long NULL_VALUE_COUNTER = 0; // used to append a number to a null value.

    private final BufferedReader reader;
    private final char delimiter;
    private final Character quoteChar;
    private byte[] currentLine;         // Bytes of the "current" line, after replacing empty values with `NULL_VALUE`.
    private boolean firstLine = true;   // becomes `false` after reading the first line.
    private int pos = 0;                // current position of the "cursor" in `currentLine`. Used by the `read()` method.
    private boolean insideQuote = false;

    public NewCSVNullInjector(InputStream in,
                              char delimiter,
                              Character quoteChar,
                              Charset charset) {
        this.reader = new BufferedReader(new InputStreamReader(in, charset));
        this.delimiter = delimiter;
        this.quoteChar = quoteChar;
        currentLine = new byte[0];
    }

    @Override
    public int read() throws IOException {
        if (currentLine.length == pos) {
            String nextLine = reader.readLine();
            if (nextLine == null) {
                return -1;
            }
            currentLine = replaceNulls(nextLine).getBytes(StandardCharsets.UTF_8);
            pos = 0;
            if (!firstLine) {
                return '\n';
            } else {
                firstLine = false;
            }
        }
        return currentLine[pos++];
    }

    String replaceNulls(final String input) {
        if (input.isEmpty() && !insideQuote) {
            return nullValue();
        }

        StringBuilder result = new StringBuilder();
        boolean checkQuotes = quoteChar != null;

        char[] inputChars = input.toCharArray();
        for (char inputChar : inputChars) {
            switch (inputChar) {    // ignore BOM characters
                case '\uFFEF', '\uFEFF': continue;
            }
            if (insideQuote) {     // then we can add whatever character, unless it's a quote again.
                if (checkQuotes && inputChar == quoteChar) {
                    insideQuote = false;
                }
            } else {                // If not inside quotes, check for empty values by checking subsequent delimiters
                if (inputChar == delimiter) {
                    // check if last char in buffer is also delimiter
                    if (result.isEmpty() || result.charAt(result.length() - 1) == delimiter) {
                        result.append(nullValue());
                    }
                } else if (checkQuotes && inputChar == quoteChar) {
                    insideQuote = true;
                }
            }
            result.append(inputChar);
        }

        // final check: if last character of buffer is separator, then it's a null value
        if (!insideQuote && result.charAt(result.length() - 1) == delimiter) {
            result.append(nullValue());
        }

        return result.toString();
    }

    private String nullValue() {
        NULL_VALUE_COUNTER++;
        return NULL_VALUE + '_' + NULL_VALUE_COUNTER;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
