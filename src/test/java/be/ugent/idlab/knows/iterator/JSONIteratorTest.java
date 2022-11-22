package be.ugent.idlab.knows.iterator;

import be.ugent.idlab.knows.access.cores.TestCore;
import be.ugent.idlab.knows.iterators.JSONSourceIterator;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JSONIteratorTest extends TestCore {
    @Test
    public void evaluate_0000_JSON() {
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator();
        try {
            jsonSourceIterator.open(makeLocalAccess("/json/0000.json","", "json", "utf-8"), "$.students[*]");
        } catch (SQLException | IOException e) {
            logger.error("An exception was thrown during the test!", e);
            fail();
        }

        assertTrue(evaluate_0000(jsonSourceIterator));
    }

    @Test
    public void evaluate_0001_JSON() {
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator();
        try {
            jsonSourceIterator.open(makeLocalAccess("/json/0001.json","", "json", "utf-8"), "$.pubs[*]");
        } catch (SQLException | IOException e) {
            logger.error("An exception was thrown during the test!", e);
            fail();
        }

        assertTrue(evaluate_0001(jsonSourceIterator));
    }
}
