package be.ugent.idlab.knows.stream;

import be.ugent.idlab.knows.access.cores.StreamTestCore;
import be.ugent.idlab.knows.streams.CSVSourceStream;
import org.junit.Test;

public class CSVStreamTest extends StreamTestCore {


    @Test
    public void eval_0000() {
        runTest("/csv/0000.csv", "src/test/resources", "csv", "utf-8", CSVSourceStream.class, this::evaluate_0000);
    }

    @Test
    public void eval_0001() {
        runTest("/csv/0001.csv", "src/test/resources", "csv", "utf-8", CSVSourceStream.class, this::evaluate_0001);
    }

    @Test
    public void eval_1001_header_long() {
        runTest("/csv/1001_header_long.csv", "src/test/resources", "csv", "utf-8", CSVSourceStream.class, this::evaluate_1001_header_long);
    }

    @Test
    public void eval_1001_header_short() {
        runTest("/csv/1001_header_short.csv", "src/test/resources", "csv", "utf-8", CSVSourceStream.class, this::evaluate_1001_header_short);
    }
}
