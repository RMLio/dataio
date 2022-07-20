package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.iterators.CSVWSourceIterator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class CSVWTest extends TestCore {

    private static final Logger logger = LoggerFactory.getLogger(CSVTest.class);

    private String[] makeArray(List<String> list){
        return list.toArray(new String[0]);
    }

    @Test
    public void evaluate_0000_CSVW(){

        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        csvwSourceIterator.open(makeLocalAccess("/csv/0000.csv"), null, List.of(), false, false);
        evaluate_0000(csvwSourceIterator, false);
    }

    @Test
    public void evaluate_0001_CSVW(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        csvwSourceIterator.open(makeLocalAccess("/csv/0001.csv"), null, List.of(), false, false);
        evaluate_0001(csvwSourceIterator);
    }

    @Test
    public void evaluate_0000_trim(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        csvwSourceIterator.open(makeLocalAccess("/csvw/0000_trim.csv"), null, List.of(), false, true);
        evaluate_0000(csvwSourceIterator, false);
    }

    @Test
    public void evaluate_1000_nulls(){
        CSVWSourceIterator csvwSourceIterator = new CSVWSourceIterator();
        csvwSourceIterator.open(makeLocalAccess("/csvw/1000_nulls.csv"), null, List.of("NULL"), false, false);

        String[] header = makeArray(List.of("ID", "Name"));

        CSVSource source1 = new CSVSource(header, makeArray(List.of("10", "Venus")), null);
        CSVSource source2 = new CSVSource(header, makeArray(List.of("12", "Serena")), null);
        CSVSource source3 = new CSVSource(header, makeArray(List.of("13", "null")), null);

        String[] array = new String[2];
        array[0] = "11";
        array[1] = null;
        CSVSource source_null = new CSVSource(header, array, null);

        compareIterator(csvwSourceIterator, Set.of(source1, source2, source3, source_null));
    }

}
