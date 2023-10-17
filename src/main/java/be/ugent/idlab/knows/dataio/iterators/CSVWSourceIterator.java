package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

public class CSVWSourceIterator extends SourceIterator {
    private static final long serialVersionUID = -5824558388620967495L;
    private final Access access;
    private final CSVWConfiguration config;
    private transient String[] header;
    private transient String[] next;
    private transient CSVReader reader;

    public CSVWSourceIterator(Access access, CSVWConfiguration config) throws Exception {
        this.access = access;
        this.config = config;
        this.bootstrap();
    }

    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    /**
     * Instantiates transient fields. This code needs to be run both at construction time and after deserialization
     */
    private void bootstrap() throws Exception {
        this.reader = new CSVReaderBuilder(new InputStreamReader(access.getInputStream(), config.getEncoding()))
                .withCSVParser(this.config.getParser())
                .withSkipLines(this.config.isSkipHeader() ? 1 : 0)
                .build();

        if (this.config.isSkipHeader()) {
            this.header = config.getHeader().toArray(new String[0]);
        } else {
            this.header = readLine();

            if (header == null) {
                throw new IllegalStateException("Unable to read the file!");
            }
        }

        this.next = readLine();
    }

    private String[] readLine() throws IOException {
        String[] line;
        do {
            try {
                line = this.reader.readNext();

                if (line == null) {
                    return null;
                }
            } catch (CsvValidationException e) {
                throw new IllegalArgumentException(String.format("File does not conform to configuration! Offending line: %s", Arrays.toString(this.reader.peek())));
            }
        } while (invalidLine(line));

        return line;
    }

    /**
     * Checks if the passed line corresponds to the filters set
     * A line is considered valid if it doesn't start with the comment prefix
     * If the first value is null, the line is accepted
     *
     * @param line line to be checked
     * @return true if the line passes all checks
     */
    private boolean invalidLine(String[] line) {
        return line[0] != null && line[0].startsWith(this.config.getCommentPrefix());
    }

    /**
     * Checks if @record has a string value which is in the nulls list, if so sets this value to null in the data map.
     *
     * @param record record to be checked
     * @return
     */
    public CSVRecord replaceNulls(CSVRecord record) {
        Map<String, String> data = record.getData();
        data.forEach((key, value) -> {
            if (value != null && this.config.getNulls().contains(value)) {
                data.put(key, null);
            }
        });
        return record;
    }

    public String[] applyTrimArray(String[] arr, String trim) {
        return Arrays.stream(arr)
                .map(item -> applyTrim(item, trim))
                .toArray(String[]::new);
    }

    public String applyTrim(String item, boolean trim) {
        if (trim) {
            return item.trim();
        }

        return item;
    }

    public String applyTrim(String item, String trim) {
        switch (trim) {
            case "true":
                return item.trim();
            case "false":
                return item;
            case "start":
                return item.stripLeading();
            case "end":
                return item.stripTrailing();
            default:
                throw new IllegalArgumentException("Unrecognized value for flag \"trim\"");
        }
    }

    @Override
    public Record next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }

        String[] line = this.next;
        try {
            this.next = readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!config.getTrim().equals("false")) {
            line = applyTrimArray(line, config.getTrim());
        }

        return replaceNulls(new CSVRecord(header, line, this.access.getDataTypes()));
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
