package be.ugent.idlab.knows.stream;

import be.ugent.idlab.knows.dataio.access.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.XMLSourceStream;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class XMLStreamTest extends StreamTestCore {

    private void runTest(String path, String basePath, String xPath, Evaluator evaluator) {
        try {
            Iterator<Source> iterator = prepareIterator(path, basePath, "xmlx", "utf-8", XMLSourceStream.class, xPath);

            assertTrue(evaluator.evaluate(iterator));
        } catch (SQLException | IOException e) {
            logger.error("An exception was thrown during the test!", e);
            fail();
        }
    }

    @Test
    public void eval_0000() {
        runTest("/xml/0000.xml", "src/test/resources", "students/student", this::evaluate_0000);
    }

    @Test
    public void eval_0001() {
        runTest("/xml/0001.xml", "src/test/resources", "pubs/pub", this::evaluate_0001);
    }
}
