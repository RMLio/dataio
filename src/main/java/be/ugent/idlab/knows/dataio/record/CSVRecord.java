package be.ugent.idlab.knows.dataio.record;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a specific implementation of a record for CSV.
 * Every record corresponds with a row of the CSV data source.
 */
public class CSVRecord extends Record {

    // The CSV record that is provided by the Apache CSVParser.
    private final Map<String, String> data;
    private final Map<String, String> datatypes;

    public CSVRecord(String[] header, String[] data, Map<String, String> datatypes) {
        this.data = new HashMap<>(header.length);
        if (header.length > data.length) {
            logger.warn("Header has more columns than this row");
        }
        if (header.length < data.length) {
            logger.warn("Header has less columns than this row, these extra values will be ignored");
        }
        for (int i = 0; i < header.length; i += 1) {
            if (i < data.length) {
                this.data.put(header[i], data[i]);
            } else {
                this.data.put(header[i], null);
            }
        }
        this.datatypes = datatypes;
    }

    /**
     * This method returns the datatype of a reference in the record.
     *
     * @param value the reference for which the datatype needs to be returned.
     * @return the IRI of the datatype.
     */
    public String getDataType(String value) {
        String datatype = null;
        if (datatypes != null) {
            datatype = datatypes.get(value);
            /*
             * Some RDBs require quotes for capitalization, but after executing the query,
             * the quotes are dropped in the results as "ID" != ID, neither is 'ID' != ID.
             *
             * If the lookup fail, remove these quotes and try again.
             */
            if (datatype == null) {
                value = value.replaceFirst("^\"", "").replaceFirst("\"$", "");
                value = value.replaceFirst("^'", "").replaceFirst("'$", "");
                datatype = datatypes.get(value);
            }
        }

        return datatype;
    }

    @Override
    public Map<String, String> getDataTypes() {
        return this.datatypes;
    }

    /**
     * This method returns the objects for a column in the CSV record (= CSV row).
     *
     * @param reference the column for which objects need to be returned.
     * @return a list of objects for the column.
     */
    @Override
    public RecordValue get(String reference) {
        String toDatabaseCase;
        if (this.data.containsKey(reference.toUpperCase())) {
            toDatabaseCase = reference.toUpperCase();
        } else if (this.data.containsKey(reference.toLowerCase())) {
            toDatabaseCase = reference.toLowerCase();
        } else {
            toDatabaseCase = reference;
        }
        if (!this.data.containsKey(toDatabaseCase)) {
            return RecordValue.notFound(String.format("Mapping for %s not found, expected one of %s", toDatabaseCase, data.keySet()));
        }
        String obj = this.data.get(toDatabaseCase);

        if (obj == null) return RecordValue.empty();
        return RecordValue.ok(obj);
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSVRecord csvRecord = (CSVRecord) o;

        if (!data.equals(csvRecord.data)) return false;
        return datatypes.equals(csvRecord.datatypes);
    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        result = 31 * result + datatypes.hashCode();
        return result;
    }

    /**
     * Writes this CSV record as a string, including headers.
     * @return This CSV record as a CSV-serialized String.
     * @throws IOException When writing to CSV goes wrong.
     */
    public String toCSVString() throws IOException {
        int size = this.data.size();
        String[] header = new String[size];
        String[] data = new String[size];

        List<String> keys = this.data.keySet().stream().toList();

        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            header[i] = key;
            data[i] = this.data.get(key);
        }

        StringWriter sw = new StringWriter();
        try (CSVWriter writer = new CSVWriter(sw)){
            writer.writeAll(List.of(header, data));
        }
        return sw.toString();
    }
}
