package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.ExcelSourceIterator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExcelIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_excel() throws Exception {
        Access access = makeLocalAccess("/excel/0000.xlsx", "", "xlsx", "utf-8");
        try (ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator(access)) {
            assertTrue(evaluate_0000(excelSourceIterator));
        }
    }

    @Test
    public void evaluate_0001_CSV() throws Exception {
        Access access = makeLocalAccess("/excel/0001.xlsx", "", "xlsx", "utf-8");
        try (ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator(access)) {
            assertTrue(evaluate_0001(excelSourceIterator));
        }
    }

    @Test
    public void evaluate_1001_header_col_missing_excel() throws Exception {
        Access access = makeLocalAccess("/excel/1001_header_col_missing.xlsx", "", "xlsx", "utf-8");
        try (ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator(access)) {

        }
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_excel() throws Exception {
        Access access = makeLocalAccess("/excel/1001_header_long.xlsx", "", "xlsx", "utf-8");
        try (ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator(access)) {
            assertTrue(evaluate_1001_header_long(excelSourceIterator));
        }
    }

    @Test
    public void evaluate_1001_header_short_excel() throws Exception {
        Access access = makeLocalAccess("/excel/1001_header_short.xlsx", "", "xlsx", "utf-8");
        try (ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator(access)) {
            assertTrue(evaluate_1001_header_short(excelSourceIterator));
        }
    }
}