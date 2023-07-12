package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.CSVSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class CSVStreamTest extends StreamTestCore {
    @Test
    public void eval_0000() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/0000.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVSourceStream stream = new CSVSourceStream(access)) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_0001() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/0001.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVSourceStream stream = new CSVSourceStream(access)) {
            runTest(stream, this::evaluate_0001);
        }
    }

    @Test
    public void eval_1001_header_long() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/1001_header_long.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVSourceStream stream = new CSVSourceStream(access)) {
            runTest(stream, this::evaluate_1001_header_long);
        }
    }

    @Test
    public void eval_1001_header_short() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/1001_header_short.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVSourceStream stream = new CSVSourceStream(access)) {
            runTest(stream, this::evaluate_1001_header_short);
        }
    }
}
