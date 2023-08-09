package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.JSONSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class JSONStreamTest extends StreamTestCore {

    @Test
    public void eval_0000() throws SQLException, IOException {
        Access access = new LocalFileAccess("json/0000.json", "src/test/resources", "json");
        try (JSONSourceStream stream = new JSONSourceStream(access, "$.students[*]")) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_0001() throws SQLException, IOException {
        Access access = new LocalFileAccess("json/0001.json", "src/test/resources", "json");
        try (JSONSourceStream stream = new JSONSourceStream(access, "$.pubs[*]")) {
            runTest(stream, this::evaluate_0001);
        }
    }

    @Test
    public void testMagicPropertyPath() throws SQLException, IOException {
        Access access = new LocalFileAccess("json/people.json", "src/test/resources", "json");
        try(JSONSourceStream stream = new JSONSourceStream(access, "$.people[*]")) {
            // consume stream into iterator
            Iterator<Source> iterator = stream.getStream().iterator();

            assertTrue(iterator.hasNext());

            JSONSource source = (JSONSource) iterator.next();

            // whole path
            assertEquals("[0,people]", source.get("_PATH").get(0));

            // indexed
            assertEquals("people", source.get("_PATH[1]").get(0));

            assertFalse(iterator.hasNext());
        }
    }
}
