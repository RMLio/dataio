package be.ugent.idlab.knows.dataio.iterators;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.utils.CSVNullInjector;
import org.simpleflatmapper.lightningcsv.CsvParser;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class CSVWSourceIterator extends SourceIterator {
    @Serial
    private static final long serialVersionUID = -5824558388620967495L;
    private static final int BUFFER_SIZE = 1024 * 128; // 128 KiB
    private final Access access;
    private final CSVWConfiguration config;
    private transient String[] header;
    private transient String[] next;
    private transient Reader inputReader;
    private transient Iterator<String[]> iterator;

    public CSVWSourceIterator(Access access, CSVWConfiguration config) throws Exception {
        this.access = access;
        this.config = config;
        this.bootstrap();
    }

    @Serial
    private void readObject(ObjectInputStream inputStream) throws Exception {
        inputStream.defaultReadObject();
        this.bootstrap();
    }

    private void bootstrap() throws Exception {
        CSVNullInjector injector = new CSVNullInjector(this.access.getInputStream(), BUFFER_SIZE, (byte) this.config.getDelimiter(), (byte) this.config.getQuoteCharacter());
        this.inputReader = new InputStreamReader(injector, this.config.getEncoding());
        CsvParser.DSL parser = config.getSFMParser(BUFFER_SIZE);
        this.iterator = parser.iterator(this.inputReader);

        if (this.config.isSkipHeader()) {
            this.header = config.getHeader().toArray(new String[0]);
        } else {
            this.header = nextLine();
        }

        this.next = nextLine();
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

            // replace any occurrence of an escaped quote with a single quote
            for (int i = 0; i < r.length; i++) {
                String s = r[i];
                // trim the string that is quoted
                if (s.startsWith("\"") && s.endsWith("\"")) {
                    s = s.substring(1, s.length() - 1);
                }

                s = s.replaceAll("\"\"", "\"");
                r[i] = s;
            }

            return r;
        }
        return null;
    }

    /**
     * Checks if @record has a string value which is in the nulls list, if so sets this value to null in the data map.
     *
     * @param record record to be checked
     * @return Checked record with defined null values replaced with {@code null}
     */
    public CSVRecord replaceNulls(CSVRecord record) {
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

    public String applyTrim(String item, String trim) {
        return switch (trim) {
            case "true" -> item.trim();
            case "false" -> item;
            case "start" -> item.stripLeading();
            case "end" -> item.stripTrailing();
            default -> throw new IllegalArgumentException("Unrecognized value for flag \"trim\"");
        };
    }

    @Override
    public Record next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        String[] line = this.next;

        this.next = nextLine();

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
        this.inputReader.close();
    }
}
