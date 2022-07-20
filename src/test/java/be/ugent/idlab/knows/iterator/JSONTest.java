package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.TestCore;
import be.ugent.idlab.knows.iterators.JSONSourceIterator;
import org.junit.Test;

public class JSONTest extends TestCore {
    @Test
    public void evaluate_0000_JSON(){
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator();
        jsonSourceIterator.open(makeLocalAccess("/json/0000.json"), "$.students[*]");
        evaluate_0000(jsonSourceIterator, false);
    }

    @Test
    public void evaluate_0001_JSON(){
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator();
        jsonSourceIterator.open(makeLocalAccess("/json/0001.json"), "$.pubs[*]");
        evaluate_0001(jsonSourceIterator);
    }
}
