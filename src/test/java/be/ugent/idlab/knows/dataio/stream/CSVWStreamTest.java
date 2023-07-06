package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.iterators.CSVWSourceIterator;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.CSVWSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class CSVWStreamTest extends StreamTestCore {
    private final CSVWConfiguration default_config = CSVWConfiguration.DEFAULT;

    private void runTest(String path, String basePath, Evaluator evaluator, CSVWConfiguration config) {
        try {
            Iterator<Source> iterator = prepareIterator(path, basePath, "csvw", "utf-8", CSVWSourceStream.class, config);
            assertTrue(evaluator.evaluate(iterator));
        } catch (SQLException | IOException e) {
            logger.error("An unexpected exception was thrown during the test!", e);
            fail();
        }
    }

    @Test
    public void eval_0000() {
        runTest("/csv/0000.csv", "src/test/resources", this::evaluate_0000, default_config);
    }

    @Test
    public void eval_0001() {
        runTest("/csv/0001.csv", "src/test/resources", this::evaluate_0001, default_config);
    }

    @Test
    public void eval_0000_trim() {
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(true).build();
        runTest("/csvw/0000_trim.csv", "src/test/resources", this::evaluate_0000, config);
    }

    @Test
    public void eval_1000_nulls() throws SQLException, IOException {
        // this test is CSVW specific, can't standardize
        Access access = makeLocalAccess("/csvw/1000_nulls.csv", "", "csvw", "utf-8");
        CSVWConfiguration config = CSVWConfiguration.builder()
                .withNulls(List.of("NULL"))
                .build();
        CSVWSourceStream stream = new CSVWSourceStream(access, config);

        try {
            stream.open(access);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        String[] header = new String[]{"ID", "Name"};

        CSVSource source1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes());
        CSVSource source2 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes());
        CSVSource source3 = new CSVSource(header, new String[]{"13", "null"}, access.getDataTypes());

        String[] array = new String[]{"11", null};
        CSVSource source_null = new CSVSource(header, array, access.getDataTypes());

        assertTrue(compareIterator(stream.getStream().iterator(), Set.of(source1, source2, source3, source_null)));
    }

    @Test
    public void eval_1001_header_long() {
        runTest("/csv/1001_header_long.csv", "src/test/resources", this::evaluate_1001_header_long, default_config);
    }

    @Test
    public void eval_1001_header_short() {
        runTest("/csv/1001_header_short.csv", "src/test/resources", this::evaluate_1001_header_short, default_config);
    }
}
