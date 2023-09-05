
package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import org.simpleflatmapper.lightningcsv.CsvParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class CSVWSourceIterator extends SourceIterator {

    private static final long serialVersionUID = -5824558388620967495L;
    private final Access access;
    private final CSVWConfiguration config;
    private transient String[] header;
    private transient String[] next;
    private transient Reader inputReader;
    private transient Iterator<String[]> iterator;

    public CSVWSourceIterator(Access access, CSVWConfiguration config) throws SQLException, IOException {
        this.access = access;
        this.config = config;
        this.bootstrap();
    }

    private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException, SQLException {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() throws SQLException, IOException {
        this.inputReader = new InputStreamReader(access.getInputStream(), config.getEncoding());

        CsvParser.DSL parser = config.getParser();
        this.iterator = parser.iterator(this.inputReader);

        if (this.config.isSkipHeader()) {
            this.header = config.getHeader().toArray(new String[0]);
        } else {
            this.header = nextLine();

            if (header == null) {
                throw new IllegalStateException("Unable to read the file!");
            }
        }

        this.next = nextLine();

        if (this.next == null) {
            throw new IllegalStateException("No further data could be read from the file!");
        }
    }

    private String[] nextLine() {
        if (this.iterator.hasNext()) {
            String[] r = this.iterator.next();
            // go over the lines till uncommented line found
            while (r[0].startsWith(config.getCommentPrefix()) && this.iterator.hasNext()) {
                r = this.iterator.next();
            }

            if (r[0].startsWith(config.getCommentPrefix())) {
                return null;
            }

            return r;
        }
        return null;
    }

    /**
     * Checks if @record has a string value which is in the nulls list, if so sets this value to null in the data map.
     *
     * @param record record to be checked
     * @return checked and possibly changed record
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

        this.next = nextLine();

        if (!config.getTrim().equals("false")) {
            line = applyTrimArray(line, config.getTrim());
        }

        return replaceNulls(new CSVSource(header, line, this.access.getDataTypes()));
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public void close() throws IOException {
        this.inputReader.close();
    }
}
