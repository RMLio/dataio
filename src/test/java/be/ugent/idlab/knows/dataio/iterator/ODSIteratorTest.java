package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.exceptions.BadHeaderException;
import be.ugent.idlab.knows.dataio.iterators.ODSSourceIterator;
import org.junit.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


public class ODSIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_ods(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/0000.ods","", "ods", "utf-8"));
        assertTrue(evaluate_0000(odsSourceIterator));
    }

    @Test
    public void evaluate_0001_ods(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/0001.ods","", "ods", "utf-8"));
        assertTrue(evaluate_0001(odsSourceIterator));
    }

    @Test
    public void evaluate_1001_header_col_missing_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        assertThrows(BadHeaderException.class, () -> odsSourceIterator.open(makeLocalAccess("/ods/1001_header_col_missing.ods","", "ods", "utf-8")));
    }

    @Test
    public void evaluate_1001_header_long_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_long.ods","", "ods", "utf-8"));
        assertTrue(evaluate_1001_header_long(odsSourceIterator));
    }

    @Test
    public void evaluate_1001_header_short_CSV() {
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        assertThrows(BadHeaderException.class, () -> odsSourceIterator.open(makeLocalAccess("/ods/1001_header_short.ods", "", "ods", "utf-8")));
    }
}
