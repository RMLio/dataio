package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.CSVSourceIterator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;


public class CSVTest extends TestCore {
    @Test
    public void evaluate_0000_CSV(){

        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/0000.csv", "","csv", "utf-8"));

        assertTrue(evaluate_0000(csvSourceIterator));
    }

    @Test
    public void evaluate_0001_CSV(){
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/0001.csv", "","csv", "utf-8"));

        assertTrue(evaluate_0001(csvSourceIterator));
    }

    @Test
    public void evaluate_1001_header_col_missing_CSV(){
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/1001_header_col_missing.csv", "","csv", "utf-8"));
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_CSV(){
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();

        csvSourceIterator.open(makeLocalAccess("/csv/1001_header_long.csv", "","csv", "utf-8"));
        assertTrue(evaluate_1001_header_long(csvSourceIterator));
    }

    @Test
    public void evaluate_1001_header_short_CSV(){
        CSVSourceIterator csvSourceIterator = new CSVSourceIterator();
        csvSourceIterator.open(makeLocalAccess("/csv/1001_header_short.csv", "","csv", "utf-8"));
        assertTrue(evaluate_1001_header_short(csvSourceIterator));
    }
}
