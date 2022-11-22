package be.ugent.idlab.knows.dataio.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a specific implementation of a record for CSV.
 * Every record corresponds with a row of the CSV data source.
 */
public class CSVSource extends Source {

    // The CSV record that is provided by the Apache CSVParser.
    private Map<String, String> data;
    private Map<String, String> datatypes;

    public CSVSource(String[] header, String[] data, Map<String, String> datatypes) {
        this.data = new HashMap<>();
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
                this.data.put(header[i], "");
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
                value = value.replaceFirst("^\'", "").replaceFirst("\'$", "");
                datatype = datatypes.get(value);
            }
        }

        return datatype;
    }

    /**
     * This method returns the objects for a column in the CSV record (= CSV row).
     *
     * @param value the column for which objects need to be returned.
     * @return a list of objects for the column.
     */
    @Override
    public List<Object> get(String value) {
        String toDatabaseCase;
        if (this.data.containsKey(value.toUpperCase())) {
            toDatabaseCase = value.toUpperCase();
        } else if (this.data.containsKey(value.toLowerCase())) {
            toDatabaseCase = value.toLowerCase();
        } else {
            toDatabaseCase = value;
        }
        if (!this.data.containsKey(toDatabaseCase)) {
            throw new IllegalArgumentException(String.format("Mapping for %s not found, expected one of %s", toDatabaseCase, data.keySet()));
        }
        String obj = this.data.get(toDatabaseCase);

        if(obj == null || obj.equals("")) return List.of();
        return List.of(obj);
    }

    public Map<String, String> getData() {
        return data;
    }

    @Override
    public boolean equals(Object object) {

        if (object == null) return false;

        if (this == object) return true;


        if (getClass() != object.getClass()) return false;

        CSVSource o = (CSVSource) object;

        return this.data.equals(o.data) &&
                ((this.datatypes != null && this.datatypes.equals(o.datatypes)) || (this.datatypes == null && o.datatypes == null));

    }

    @Override
    public int hashCode() {
        int hash = 31 * 7 + (data == null ? 0 : data.hashCode());
        return 31 * hash + (datatypes == null ? 0 : datatypes.hashCode());
    }
}
