package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class CSVWSourceIterator2 extends SourceIterator implements Serializable {
    private HashMap<String, String> dataTypes;
    private CSVWConfiguration config;
    private String[] header;
    private String[] next;

    private Access access;

    private transient CSVReader reader;

    public CSVWSourceIterator2(Access access) {
        this.access = access;
    }

    private void constructReader() throws SQLException, IOException {
        this.reader = new CSVReaderBuilder(new InputStreamReader(access.getInputStream(), config.getEncoding()))
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .withCSVParser(this.config.getParser())
                .withSkipLines(config.isSkipHeader() ? 1 : 0)
                .build();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        inputStream.defaultReadObject();
        constructReader();
    }

    public void open(Access access, CSVWConfiguration config) throws SQLException, IOException, CsvValidationException {
        this.config = config;
        this.dataTypes = access.getDataTypes();
        constructReader();
        reader.peek();

        // read the header
        if (config.isSkipHeader()) {
            this.header = config.getHeader().toArray(new String[0]);
        } else { // the first parsed record is the header
            this.header = readLine();

            if (header == null) {
                throw new IllegalStateException("No header could be read from the file");
            }
        }

        // read in the next record
        this.next = readLine();

        if (this.next == null) {
            // TODO: discuss the necessity of this
            throw new IllegalStateException("No data could be read from file");
        }
    }

    private String[] readLine() throws CsvValidationException, IOException {
        String[] line;
        do {
            line = this.reader.readNext();
            if (line == null) { // next returned a null
                return null;
            }

        } while ((line.length == 0 || invalidLine(line)) && this.reader.peek() != null);

        if (invalidLine(line)) { // no more records can be read
            return null;
        }

        return line;
    }

    /**
     * Checks if the passed line corresponds to the filters set
     *
     * @param line line to be checked
     * @return true if the line passes all checks
     */
    private boolean invalidLine(String[] line) {
        return line.length == 0 ||  // line is empty
                line[0].startsWith(this.config.getCommentPrefix()); // line does starts with a comment prefix
    }

    /**
     * Checks if @record has a string value which is in the nulls list, if so sets this value to null in the data map.
     *
     * @param record
     * @return
     */
    public CSVSource replaceNulls(CSVSource record) {
        Map<String, String> data = record.getData();
        data.forEach((key, value) -> {
            if (this.config.getNulls().contains(value)) {
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
    public Source next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }

        String[] line = this.next;
        try {
            this.next = readLine();
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }

        if (!config.getTrim().equals("false")) {
            line = applyTrimArray(line, config.getTrim());
        }

        return replaceNulls(new CSVSource(header, line, dataTypes));
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }
}
