package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.access.Access;
import be.ugent.idlab.knows.access.LocalFileAccess;
import be.ugent.idlab.knows.source.CSVSource;
import be.ugent.idlab.knows.source.CSVSourceIterator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class CSVTest extends TestCore {

    private static final Logger logger = LoggerFactory.getLogger(CSVTest.class);

    private String[] makeArray(List<String> list){
        return list.toArray(new String[0]);
    }

    @Test
    public void evaluate_0000_CSV(){
        String[] header = makeArray(List.of("ID", "Name"));
        compareIterator(new CSVSourceIterator(makeLocalAccess("/csv/0000.csv")), Set.of(new CSVSource(header, makeArray(List.of("10", "Venus")), null)));
    }

    @Test
    public void evaluate_0001_CSV(){
        String[] header = makeArray(List.of("name","description","address"));
        CSVSource source1 = new CSVSource(header, makeArray(List.of("Trollekelder","Beer café in the shadows of the St James' church","Bij Sint-Jacobs 17 9000 Gent Belgium")), null);
        CSVSource source2 = new CSVSource(header, makeArray(List.of("Hot Club Gent","Live jazz concerts in an intimate setting","Schuddevisstraatje 2 - Groentenmarkt 9000 Gent Belgium")), null);
        CSVSource source3 = new CSVSource(header, makeArray(List.of("Jan van Gent","Quirky local pub with a popular summer terrace","Annonciadenstraat 1 9000 Gent Belgium")), null);

        compareIterator(new CSVSourceIterator(makeLocalAccess("/csv/0001.csv")), Set.of(source1, source2, source3));
    }
}
