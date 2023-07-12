package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.JSONSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

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
}
