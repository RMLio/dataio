package be.ugent.idlab.knows.dataio.source;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is a specific implementation of a Record for Excel.
 * Every record corresponds with a row of the Excel file.
 */
public class ExcelSource extends Source {

    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, String> data_types = new HashMap<>();

    public ExcelSource(Row header, Row row) {
        if (header.getLastCellNum() > row.getLastCellNum()) {
            logger.warn("Header has more columns than this row, these will be filled with empty strings");
        }
        if (header.getLastCellNum() < row.getLastCellNum()) {
            logger.warn("Header has less columns than this row, these extra values will be ignored");
        }
        for (int i = 0; i < header.getLastCellNum(); i++) {
            if (i < row.getLastCellNum()) {
                if (header.getCell(i) == null) {
                    continue;
                }
                data.put(header.getCell(i).getStringCellValue(), getCellValue(row.getCell(i)));
                data_types.put(header.getCell(i).getStringCellValue(), getIRI(row.getCell(i)));
            } else {
                data.put(header.getCell(i).getStringCellValue(), null);
                data_types.put(header.getCell(i).getStringCellValue(), "");
            }
        }
    }

    /**
     * This method returns the datatype of a reference in the record.
     *
     * @param value the reference for which the datatype needs to be returned.
     * @return the IRI of the datatype.
     */
    public String getDataType(String value) {
        return data_types.getOrDefault(value, "");
    }

    @Override
    public Map<String, String> getDataTypes() {
        return this.data_types;
    }

    @Override
    public boolean equals(Object obj) {

        if (this.getClass() != obj.getClass()) return false;

        //TODO other object could have more columns in row then this.row and this would still return true
        ExcelSource excelSource = (ExcelSource) obj;
        for (String value : this.data.keySet()) {

            if (!this.get(value).equals(excelSource.get(value)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data, data_types);
    }

    /**
     * This method returns the objects for a column in the Excel record (= Excel row).
     *
     * @param value the column for which objects need to be returned.
     * @return a list of objects for the column.
     */
    @Override
    public List<Object> get(String value) {
        Object obj = data.getOrDefault(value, null);
        if (obj == null) return List.of();
        return List.of(obj);
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) return null;
        Object obj;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    double d = cell.getNumericCellValue();
                    // Cast to int if needed
                    if (d % 1 == 0) {
                        obj = (int) d;
                    } else {
                        obj = d;
                    }
                    break;

                case BOOLEAN:
                    obj = cell.getBooleanCellValue();
                    break;
                default:
                    obj = cell.getStringCellValue();
                    break;
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a CellType to a XSD datatype URI
     *
     * @param cell
     * @return
     */
    public static String getIRI(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                return cell.getNumericCellValue() % 1 == 0 ? XSDDatatype.XSDinteger.getURI() : XSDDatatype.XSDdouble.getURI();
            case BOOLEAN:
                return XSDDatatype.XSDboolean.getURI();
            default:
                return XSDDatatype.XSDstring.getURI();
        }
    }

    public void printString() {
        for (String value : this.data.keySet()) {
            System.out.print(value + ": ");
            System.out.println(this.get(value));
        }
    }
}

