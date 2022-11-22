package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.access.cores.TestCore;
import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.iterators.CSVWSourceIterator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class CSVWIteratorTest extends TestCore {
    private static final Logger logger = LoggerFactory.getLogger(CSVIteratorTest.class);

    private void defaultOpen(CSVWSourceIterator iterator, String inputFile){
        iterator.open(makeLocalAccess(inputFile,"", "csvw", "utf-8"), null, List.of(), false, false);
    }

    @Test
    public void evaluate_0000_CSVW(){

        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "/csv/0000.csv");
//        csvwSourceIterator.open(makeLocalAccess("/csv/0000.csv"), null, List.of(), false, false);
        assertTrue(evaluate_0000(csvwSourceIterator));
    }

    @Test
    public void evaluate_0001_CSVW(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "/csv/0001.csv");
//        csvwSourceIterator.open(makeLocalAccess("/csv/0001.csv"), null, List.of(), false, false);
        assertTrue(evaluate_0001(csvwSourceIterator));
    }

    @Test
    public void evaluate_0000_trim(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        csvwSourceIterator.open(makeLocalAccess("/csvw/0000_trim.csv","", "csvw", "utf-8"), null, List.of(), false, true);
        assertTrue(evaluate_0000(csvwSourceIterator));
    }

    @Test
    public void evaluate_1000_nulls(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        Access access = makeLocalAccess("/csvw/1000_nulls.csv","", "csvw", "utf-8");
        csvwSourceIterator.open(access, null, List.of("NULL"), false, false);

        String[] header = new String[]{"ID", "Name"};

        CSVSource source1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes());
        CSVSource source2 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes());
        CSVSource source3 = new CSVSource(header, new String[]{"13", "null"}, access.getDataTypes());

        String[] array = new String[]{"11", null};
        CSVSource source_null = new CSVSource(header, array, access.getDataTypes());

        assertTrue(compareIterator(csvwSourceIterator, Set.of(source1, source2, source3, source_null)));
    }

    @Test
    public void evaluate_1001_header_col_missing_CSVW(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "/csv/1001_header_col_missing.csv");
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_CSVW(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "/csv/1001_header_long.csv");
        assertTrue(evaluate_1001_header_long(csvwSourceIterator));
    }

    @Test
    public void evaluate_1001_header_short_CSVW(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        defaultOpen(csvwSourceIterator, "/csv/1001_header_short.csv");
        assertTrue(evaluate_1001_header_short(csvwSourceIterator));
    }

}
