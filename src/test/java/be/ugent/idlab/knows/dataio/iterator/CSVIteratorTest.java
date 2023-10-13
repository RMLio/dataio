package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVIteratorTest extends TestCore {
    @Test
    public void evaluate_0000_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/0000.csv", "", "csv", "utf-8");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            assertTrue(evaluate_0000(iterator));
        }
    }

    @Test
    public void evaluate_0001_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/0001.csv", "", "csv", "utf-8");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            assertTrue(evaluate_0001(iterator));
        }
    }

    @Test
    @Disabled
    public void evaluate_1001_header_col_missing_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/1001_header_col_missing.csv", "", "csv", "utf-8");
        try (CSVSourceIterator csvSourceIterator = new CSVSourceIterator(access)) {
            //TODO should fail, check if it does
        }
    }

    @Test
    public void evaluate_1001_header_long_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/1001_header_long.csv", "", "csv", "utf-8");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            assertTrue(evaluate_1001_header_long(iterator));
        }
    }

    @Test
    public void evaluate_1001_header_short_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/1001_header_short.csv", "", "csv", "utf-8");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            assertTrue(evaluate_1001_header_short(iterator));
        }
    }

    @Test
    public void evaluate_0002_BOM_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/0002_BOM.csv", "", "csv", "utf-8");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            assertTrue(evaluate_0002_BOM(iterator));
        }
    }

    @Test
    public void evaluateSparseInput() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/sparseInput.csv", "", "csv", "utf-8");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            assertTrue(iterator.hasNext());

            // check first source
            CSVRecord source = (CSVRecord) iterator.next();
            Map<String, String> expected = new HashMap<>() {{
                put("A", "1");
                put("B", null);
                put("C", "3");
            }};
            Map<String, String> actual = source.getData();

            assertEquals(expected, actual);
        }
    }

    /**
     * Tests correct reading of empty CSV values into null values.
     */
    @Test
    public void test_missing_values() throws SQLException, IOException {
        Access access = new LocalFileAccess("csv/null_value.csv", "src/test/resources", "csv");

        String[] header = new String[]{"ID", "Name", "Age"};
        // must be able to recognise missing value at the beginning
        List<CSVRecord> expectedValues = List.of(
                new CSVRecord(header, new String[]{"1", "Foo", ""}, access.getDataTypes()), // empty string is accepted
                new CSVRecord(header, new String[]{"2", null, "2"}, access.getDataTypes()), // empty value in the middle is recognised as null
                new CSVRecord(header, new String[]{null, "Bar", "3"}, access.getDataTypes()) // empty value in the beginning is also accepted
        );

        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            for (CSVRecord expected : expectedValues) {
                CSVRecord actual = (CSVRecord) iterator.next();
                assertEquals(expected.getData(), actual.getData());
            }
        }
    }
}
