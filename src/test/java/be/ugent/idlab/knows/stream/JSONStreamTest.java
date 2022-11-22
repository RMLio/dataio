package be.ugent.idlab.knows.stream;

import be.ugent.idlab.knows.dataio.access.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.JSONSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JSONStreamTest extends StreamTestCore {

    private void runTest(String path, String basePath, Evaluator evaluator, String jsonPath) {
        try {
            Iterator<Source> iterator = prepareIterator(path, basePath, "json", "utf-8", JSONSourceStream.class, jsonPath);

            assertTrue(evaluator.evaluate(iterator));
        } catch (SQLException | IOException e) {
            logger.error("An exception was thrown during the test!", e);
            fail();
        }
    }

    @Test
    public void eval_0000() {
        runTest("/json/0000.json", "src/test/resources", this::evaluate_0000, "$.students[*]");
    }

    @Test
    public void eval_0001() {
        runTest("/json/0001.json", "src/test/resources", this::evaluate_0001, "$.pubs[*]");
    }
}
