package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.CSVWSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CSVWStreamTest extends StreamTestCore {

    // defaults arguments for CSVWSourceStream
    private final Object[] defaultArgs = new Object[]{null, List.of(), false, false};

    private void runTest(String path, String basePath, Evaluator evaluator, Object... initArgs) {

        Object[] init = defaultArgs;
        if (initArgs.length != 0) {
            init = initArgs;
        }

        try {
            Iterator<Source> iterator = prepareIterator(path, basePath, "csvw", "utf-8", CSVWSourceStream.class, init);
            assertTrue(evaluator.evaluate(iterator));
        } catch (SQLException | IOException e) {
            logger.error("An unexpected exception was thrown during the test!", e);
            fail();
        }
    }

    @Test
    public void eval_0000() {
        runTest("/csv/0000.csv", "src/test/resources", this::evaluate_0000);
    }

    @Test
    public void eval_0001() {
        runTest("/csv/0001.csv", "src/test/resources", this::evaluate_0001);
    }

    @Test
    public void eval_0000_trim() {
        Object[] initArgs = new Object[]{null, List.of(), false, true};
        runTest("/csvw/0000_trim.csv", "src/test/resources", this::evaluate_0000, initArgs);
    }

    @Test
    public void eval_1000_nulls() {
        // this test is CSVW specific, can't standardize
        CSVWSourceStream stream = new CSVWSourceStream(null, List.of("NULL"), false, false);
        Access access = makeLocalAccess("/csvw/1000_nulls.csv", "", "csvw", "utf-8");

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
        runTest("/csv/1001_header_long.csv", "src/test/resources", this::evaluate_1001_header_long);
    }

    @Test
    public void eval_1001_header_short() {
        runTest("/csv/1001_header_short.csv", "src/test/resources", this::evaluate_1001_header_short);
    }
}
