package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class JSONIteratorTest extends TestCore {
    @Test
    public void evaluate_0000_JSON() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/0000.json", "", "json", "utf-8");
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.students[*]");

        assertTrue(evaluate_0000(jsonSourceIterator));
    }

    @Test
    public void evaluate_0001_JSON() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/0001.json", "", "json", "utf-8");
        JSONSourceIterator jsonSourceIterator = new JSONSourceIterator(access, "$.pubs[*]");

        assertTrue(evaluate_0001(jsonSourceIterator));
    }
}
