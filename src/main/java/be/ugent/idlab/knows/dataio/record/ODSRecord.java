package be.ugent.idlab.knows.dataio.record;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;

import java.util.*;

/**
 * This class is a specific implementation of a record for ODS.
 * Every record corresponds with a row of the ODS data source.
 */
public class ODSRecord extends Record {
    private Row row;
    private Map<String, Cell> header = new HashMap<>();

    public ODSRecord(Row header, Row row) {
        // get name from first row and types from second row
        Row nextRow = header.getNextRow();
        for (int i = 0; i < header.getCellCount(); i++) {
            Cell cell = header.getCellByIndex(i);
            this.header.put(cell.getStringValue(), nextRow.getCellByIndex(i));
        }

        this.row = row;
    }

    /**
     * Convert a cell type to a XSD datatype URI
     *
     * @param cellType
     * @return
     */
    public static String getIRI(String cellType) {
//        https://odftoolkit.org/api/simple/org/odftoolkit/simple/table/Cell.html#getValueType--
        if (cellType == null) {
            return "";
        }
        switch (cellType) {
            case "boolean":
                return XSDDatatype.XSDboolean.getURI();
            case "float":
                return XSDDatatype.XSDdouble.getURI();
            default: // String URI by default
                return XSDDatatype.XSDstring.getURI();
//        }
        }
    }

    /**
     * This method returns the datatype of a reference in the record.
     *
     * @param value the reference for which the datatype needs to be returned.
     * @return the IRI of the datatype.
     */
    public String getDataType(String value) {
        String cellType = null;

        if (header != null && header.get(value) != null) {
            cellType = header.get(value).getValueType();
        }
        return getIRI(cellType);
    }

    /**
     * This method returns the objects for a column in the ODS record (= ODS row).
     *
     * @param value the column for which objects need to be returned.
     * @return a list of objects for the column.
     */
    @Override
    public List<Object> get(String value) {
        List<Object> result = new ArrayList<>();
        Object obj;
        try {
            int index = header.get(value).getColumnIndex();
            Cell cell = row.getCellByIndex(index);
            switch (cell.getValueType()) {
                case "boolean":
                    obj = cell.getBooleanValue();
                    break;
                case "float":
                    double d = cell.getDoubleValue();
                    // Cast to int if needed
                    if (d % 1 == 0) {
                        obj = (int) d;
                    } else {
                        obj = d;
                    }
                    break;
                case "string":
                default:
                    obj = cell.getStringValue();
                    break;
            }
            // TODO don't stringify all types, but retain them
            // needs object comparison in join function
            // FunctionModel
            // java.lang.IllegalArgumentException: argument type mismatch
            obj = String.valueOf(obj);
            result.add(obj);
        } catch (Exception e) {
            return result;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ODSRecord odsRecord = (ODSRecord) o;
        return rowEquals(odsRecord.row) && headerEquals(odsRecord.header);
    }

    /**
     * Compares the headers.
     * This method is implemented because of the lack of proper equals() method in Cell class we depend on.
     *
     * @param otherHeader header to compare with the header of this
     * @return true if the headers are equivalent, false otherwise
     */
    private boolean headerEquals(Map<String, Cell> otherHeader) {
        if (this.header.keySet().equals(otherHeader.keySet())) {
            for (String header : this.header.keySet()) {
                Cell thisCell = this.header.get(header);
                Cell otherCell = otherHeader.get(header);

                if (!cellEquals(thisCell, otherCell)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Compares the row of this record with another.
     * This method is implemented because of the lack of proper equals() method in the library we depend on.
     * The comparison is based on the cell count and cell contents.
     *
     * @param row row to compare with the row of this
     * @return true if the rows are equivalent, false otherwise
     */
    private boolean rowEquals(Row row) {
        if (this.row.getCellCount() == row.getCellCount()) {
            for (int i = 0; i < this.row.getCellCount(); i++) {
                if (!cellEquals(i, row)) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    /**
     * Compares a cell at cellIndex with another cell in row at the same index.
     * This method is implemented because of the lack of proper equals() method in the library we depend on.
     * The comparison is based on the cell type and cell content
     *
     * @param cellIndex index of the cell to compare
     * @param row       row to find the cell in
     * @return true if the cells are equivalent, false otherwise
     */
    private boolean cellEquals(int cellIndex, Row row) {
        Cell thisCell = this.row.getCellByIndex(cellIndex);
        Cell otherCell = row.getCellByIndex(cellIndex);

        // check that both cells have the same value type and same string contents
        return cellEquals(thisCell, otherCell);
    }

    /**
     * Compares two cells.
     * Two cells are equivalent if the type of their values is the same and the string value of their contents is the same
     *
     * @param cell
     * @param otherCell
     * @return true if the cells are equivalent, false otherwise
     */
    private boolean cellEquals(Cell cell, Cell otherCell) {
        return cell.getValueType().equals(otherCell.getValueType()) && cell.getStringValue().equals(otherCell.getStringValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, header);
    }
}
