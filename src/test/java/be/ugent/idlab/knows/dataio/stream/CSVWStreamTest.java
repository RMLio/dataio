package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import be.ugent.idlab.knows.dataio.streams.CSVWSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class CSVWStreamTest extends StreamTestCore {
    private final CSVWConfiguration default_config = CSVWConfiguration.DEFAULT;

    @Test
    public void eval_0000() throws SQLException, IOException {
        Access access = new LocalFileAccess("csv/0000.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVWSourceStream stream = new CSVWSourceStream(access, default_config)) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_0001() throws SQLException, IOException {
        Access access = new LocalFileAccess("csv/0001.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVWSourceStream stream = new CSVWSourceStream(access, default_config)) {
            runTest(stream, this::evaluate_0001);
        }
    }

    @Test
    public void eval_0000_trim() throws SQLException, IOException {
        Access access = new LocalFileAccess("csvw/0000_trim.csv", "src/test/resources", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(true).build();

        try (CSVWSourceStream stream = new CSVWSourceStream(access, config)) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_1000_nulls() throws SQLException, IOException {
        Access access = new LocalFileAccess("csvw/1000_nulls.csv", "src/test/resources", "csvw");
        CSVWConfiguration config = CSVWConfiguration.builder()
                .withNulls(List.of("NULL"))
                .build();

        String[] header = new String[]{"ID", "Name"};

        CSVRecord source1 = new CSVRecord(header, new String[]{"10", "Venus"}, access.getDataTypes());
        CSVRecord source2 = new CSVRecord(header, new String[]{"12", "Serena"}, access.getDataTypes());
        CSVRecord source3 = new CSVRecord(header, new String[]{"13", "null"}, access.getDataTypes());

        String[] array = new String[]{"11", null};
        CSVRecord source_null = new CSVRecord(header, array, access.getDataTypes());

        try (CSVWSourceStream stream = new CSVWSourceStream(access, config)) {
            assertTrue(compareIterator(stream.getStream().iterator(), Set.of(source1, source2, source3, source_null)));
        }
    }

    @Test
    public void eval_1001_header_long() throws SQLException, IOException {
        Access access = new LocalFileAccess("csv/1001_header_long.csv", "src/test/resources", "csv");
        try (CSVWSourceStream stream = new CSVWSourceStream(access, default_config)) {
            runTest(stream, this::evaluate_1001_header_long);
        }
    }

    @Test
    public void eval_1001_header_short() throws SQLException, IOException {
        Access access = new LocalFileAccess("csv/1001_header_short.csv", "src/test/resources", "csv");
        try (CSVWSourceStream stream = new CSVWSourceStream(access, default_config)) {
            runTest(stream, this::evaluate_1001_header_short);
        }
    }
}
