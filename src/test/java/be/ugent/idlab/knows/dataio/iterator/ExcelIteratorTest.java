package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.ExcelSourceIterator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ExcelIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_excel() {
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/0000.xlsx", "", "xlsx", "utf-8"));
        assertTrue(evaluate_0000(excelSourceIterator));
    }

    @Test
    public void evaluate_0001_CSV() {
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/0001.xlsx", "", "xlsx", "utf-8"));
        assertTrue(evaluate_0001(excelSourceIterator));
    }

    @Test
    public void evaluate_1001_header_col_missing_excel() {
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/1001_header_col_missing.xlsx", "", "xlsx", "utf-8"));
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_excel() {
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/1001_header_long.xlsx", "", "xlsx", "utf-8"));
        assertTrue(evaluate_1001_header_long(excelSourceIterator));
    }

    @Test
    public void evaluate_1001_header_short_excel() {
        ExcelSourceIterator excelSourceIterator = new ExcelSourceIterator();
        excelSourceIterator.open(makeLocalAccess("/excel/1001_header_short.xlsx", "", "xlsx", "utf-8"));
        assertTrue(evaluate_1001_header_short(excelSourceIterator));
    }

}
