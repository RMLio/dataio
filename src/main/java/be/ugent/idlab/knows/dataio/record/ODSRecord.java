package be.ugent.idlab.knows.dataio.record;

import be.ugent.idlab.knows.dataio.exceptions.UnequalHeaderRowLengthException;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ODSRecord extends Record {

    private final Map<String, Cell> values;

    public ODSRecord(Row header, Row row) {
        this.values = new HashMap<>();

        if (header.getCellCount() != row.getCellCount()) {
            throw new UnequalHeaderRowLengthException(header.toString(), row.toString());
        }


        for (int i = 0; i < header.getCellCount(); i++) {
            Cell headerCell = header.getCellByIndex(i);
            Cell valueCell = row.getCellByIndex(i);

            this.values.put(headerCell.getStringValue(), valueCell);
        }
    }

    private static Object getValueFromCell(Cell cell) {
        Object out;
        switch (cell.getValueType()) {
            case "boolean":
                out = cell.getBooleanValue();
                break;
            case "float":
                Double d = cell.getDoubleValue();
                if (d % 1.0 == 0.0) {
                    out = d.intValue();
                } else {
                    out = d;
                }
                break;
            default:
                out = cell.getStringValue();
                break;
        }

        out = String.valueOf(out);
        return out;
    }

    @Override
    public List<Object> get(String reference) {
        List<Object> result = new ArrayList<>();

        Cell cell = this.values.get(reference);

        // if the reference is unknown or if the cell is empty, return an empty list
        // Empty cell can be recognized by Cell::getValueType returning null
        if (cell == null || cell.getValueType() == null) {
            return result;
        }

        Object out = getValueFromCell(cell);
        result.add(out);

        return result;
    }

    @Override
    public String getDataType(String reference) {
        Cell cell = this.values.get(reference);

        if (cell == null) {
            return null;
        }

        switch (cell.getValueType()) {
            case "boolean":
                return XSDDatatype.XSDboolean.getURI();
            case "float":
                // here, it could be an integer, or it could be a double
                double d = cell.getDoubleValue();
                if (d % 1.0 == 0.0) { // no decimal part, therefore an integer
                    return XSDDatatype.XSDinteger.getURI();
                }
                return XSDDatatype.XSDdouble.getURI();
            case "string":
            default:
                return XSDDatatype.XSDstring.getURI();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ODSRecord that = (ODSRecord) o;

        for (Map.Entry<String, Cell> e : this.values.entrySet()) {
            Cell thisCell = e.getValue();
            Cell thatCell = that.values.get(e.getKey());

            if (!cellEquals(thisCell, thatCell)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares two cells.
     * Two cells are equivalent if they have the same value type and the same string values
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
        return values.hashCode();
    }
}
