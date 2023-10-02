package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.JSONSourceIterator;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


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

    /**
     * Tests the magic property path indexing
     */
    @Test
    public void testMagicPropertyPathIndexing() throws SQLException, IOException {
        Access access = new LocalFileAccess("json/people.json", "src/test/resources", "json");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.people[*]")) {
            assertTrue(iterator.hasNext());
            JSONSource source = (JSONSource) iterator.next();
            // sanity check
            assertEquals("John", source.get("firstName").get(0));

            // grab the whole path
            assertEquals("[0,people]", source.get("\\_PATH").get(0));
            // index the path
            assertEquals("people", source.get("\\_PATH[1]").get(0));
            assertFalse(iterator.hasNext());
        }
    }

    /**
     * Tests referencing the real _PATH reference.
     * To obtain the real property, the _ must be escaped
     */
    @Test
    public void testMagicPropertyEscapedPath() throws SQLException, IOException {
        Access access = new LocalFileAccess("json/people.json", "src/test/resources", "json");
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.people[*]")) {
            assertTrue(iterator.hasNext());
            JSONSource source = (JSONSource) iterator.next();
            // real property
            assertEquals("foo", source.get("_PATH").get(0));
            // magic property
            assertEquals("[0,people]", source.get("\\_PATH").get(0));
            assertNull(source.get("\\\\_PATH"));
        }
    }
}
