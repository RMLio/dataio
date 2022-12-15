package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class CSVIteratorTest extends TestCore {
    @Test
    public void evaluate_0000_CSV() {

        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/0000.csv", "", "csv", "utf-8"));

        assertTrue(evaluate_0000(csvSourceIterator));
    }

    @Test
    public void evaluate_0001_CSV() {
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/0001.csv", "", "csv", "utf-8"));

        assertTrue(evaluate_0001(csvSourceIterator));
    }

    @Test
    public void evaluate_1001_header_col_missing_CSV() {
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/1001_header_col_missing.csv", "", "csv", "utf-8"));
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_CSV() {
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();

        csvSourceIterator.open(makeLocalAccess("/csv/1001_header_long.csv", "", "csv", "utf-8"));
        assertTrue(evaluate_1001_header_long(csvSourceIterator));
    }

    @Test
    public void evaluate_1001_header_short_CSV() {
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/1001_header_short.csv", "", "csv", "utf-8"));
        assertTrue(evaluate_1001_header_short(csvSourceIterator));
    }

    @Test
    public void evaluate_0002_BOM_CSV() {
        CSVSourceIterator iterator = new CSVSourceIterator();
        iterator.open(makeLocalAccess("/csv/0002_BOM.csv", "", "csv", "utf-8"));

        assertTrue(evaluate_0002_BOM(iterator));
    }
}
