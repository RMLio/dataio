package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.iterators.ODSSourceIterator;
import org.junit.Test;


public class ODSTest extends TestCore {


    @Test
    public void evaluate_0000_ods(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/0000.ods","", "ods", "ods"));
        evaluate_0000(odsSourceIterator, false);
    }

    @Test
    public void evaluate_0001_ods(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/0001.ods","", "ods", "ods"));
        evaluate_0001(odsSourceIterator);
    }

    @Test
    public void evaluate_1001_header_col_missing_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_col_missing.ods","", "ods", "ods"));
        //TODO should fail, check if it does
    }

    @Test
    public void evaluate_1001_header_long_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_long.ods","", "ods", "ods"));
        evaluate_1001_header_long(odsSourceIterator);
    }

    @Test
    public void evaluate_1001_header_short_CSV(){
        ODSSourceIterator odsSourceIterator = new ODSSourceIterator();
        odsSourceIterator.open(makeLocalAccess("/ods/1001_header_short.ods","", "ods", "ods"));
        evaluate_1001_header_short(odsSourceIterator);
    }
}
