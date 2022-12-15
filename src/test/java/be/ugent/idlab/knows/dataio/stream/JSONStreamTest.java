package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.source.Source;
import be.ugent.idlab.knows.dataio.streams.JSONSourceStream;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.DisabledIf;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Consumer;

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


    public boolean filePresent() {
        boolean v = new File("src/test/resources/json/test.json").exists();
        System.out.println(v);
        return new File("src/test/resources/json/test.json").exists();
    }
    @Test
    @DisabledIf("filePresent")
    public void test_memory_usage() throws SQLException, IOException {
        Assume.assumeTrue(filePresent());

        JSONSourceStream stream = new JSONSourceStream("$.*");
        // download and generate using this https://gitlab.ilabt.imec.be/rml/proc/rmlmeamer-components/cocktail/-/blob/main/cocktail.py
        // putting this file to history is a *bad* idea
        Access access = makeLocalAccess("/json/test.json", "src/test/resources", "csv", "utf-8");
        stream.open(access);

        stream.getStream().forEach(new Consumer<>() {
            int count = 0;

            @Override
            public void accept(Source source) {
                if (count < 10) {
                    System.out.println(source);
                }
                count++;
            }
        });

        printMemoryStatistics();
    }
}
