package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
            Map<String, String> expected = new HashMap<>(){{
                put("A", "1");
                put("B", null);
                put("C", "3");
            }};
            Map<String, String> actual = source.getData();

            assertEquals(expected, actual);
        }
    }
}
