package be.ugent.idlab.knows.dataio.source;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a specific implementation of a record for ODS.
 * Every record corresponds with a row of the ODS data source.
 */
public class ODSSource extends Source {
    private Row row;
    private Map<String, String> data_types = new HashMap<>();
    private final Map<String, Object> data = new HashMap<>();

    public ODSSource(String[] header, String[] data, Map<String, String> datatypes) {
        this.data_types = datatypes;

        for(int i = 0; i < header.length; i++) {
            if (header[i].equals("")) {
                throw new RuntimeException("Empty header field!");
            }

            this.data.put(header[i], data[i]);
        }
    }

    public ODSSource(Row header, Row row) {
        for (int i = 0; i < header.getCellCount(); i++) {
            Cell cell = header.getCellByIndex(i);
            data.put(cell.getStringValue(), getCellValue(row.getCellByIndex(i)));
            data_types.put(cell.getStringValue(), getIRI(cell.getValueType()));
        }
        this.row = row;
    }

    private Object getCellValue(Cell cell){
        try{
            switch (cell.getValueType()) {
                case "boolean":
                    return
                            cell.getBooleanValue();
                case "float":
                    double d = cell.getDoubleValue();
                    // Cast to int if needed
                    if (d % 1 == 0) {
                        return (int) d;
                    } else {
                        return d;
                    }
                default:
                    return cell.getStringValue();
            }
        // TODO don't stringify all types, but retain them
    } catch (Exception e) {
        return null;
    }
 }

    /**
     * This method returns the datatype of a reference in the record.
     * @param value the reference for which the datatype needs to be returned.
     * @return the IRI of the datatype.
     */
    public String getDataType(String value) {
        return data_types.getOrDefault(value, "");
    }

    @Override
    public boolean equals(Object obj) {
        if(this.getClass() != obj.getClass()) return false;

        //TODO other object could have more columns in row then this.row and this would still return true
        ODSSource odsSource = (ODSSource) obj;
        for(String value: this.data.keySet()){

            if(! this.get(value).equals(odsSource.get(value)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        //TODO
        return 1;
    }

    /**
     * This method returns the objects for a column in the ODS record (= ODS row).
     * @param value the column for which objects need to be returned.
     * @return a list of objects for the column.
     */
    @Override
    public List<Object> get(String value) {
        Object obj = data.getOrDefault(value, null);
        if(obj == null) return List.of();
        return List.of(obj);
    }

    /**
     * Convert a cell type to a XSD datatype URI
     * @param cellType
     * @return
     */
    public static String getIRI(String cellType) {
//        https://odftoolkit.org/api/simple/org/odftoolkit/simple/table/Cell.html#getValueType--
        if (cellType == null) {
            return "";
        }
        switch (cellType) {
            case "boolean": return XSDDatatype.XSDboolean.getURI();
            case "float": return XSDDatatype.XSDdouble.getURI();
            default: return XSDDatatype.XSDstring.getURI();
        }
    }
}
