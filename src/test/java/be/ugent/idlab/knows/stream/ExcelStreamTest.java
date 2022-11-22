package be.ugent.idlab.knows.stream;

import be.ugent.idlab.knows.dataio.access.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.ExcelSourceStream;
import org.junit.Test;

public class ExcelStreamTest extends StreamTestCore {

    @Test
    public void eval_0000() {
        runTest("/excel/0000.xlsx", "src/test/resources", "xlsx", "xlsx", ExcelSourceStream.class, this::evaluate_0000);
    }

    @Test
    public void eval_0001() {
        runTest("/excel/0001.xlsx", "src/test/resources","xlsx", "xlsx", ExcelSourceStream.class, this::evaluate_0001);
    }

    @Test
    public void eval_1001_header_long_excel() {
        runTest("/excel/1001_header_long.xlsx", "src/test/resources", "xlsx", "xlsx", ExcelSourceStream.class, this::evaluate_1001_header_long);
    }

    @Test
    public void eval_1001_header_short_excel() {
        runTest("/excel/1001_header_short.xlsx", "src/test/resources", "xlsx", "xlsx", ExcelSourceStream.class, this::evaluate_1001_header_short);
    }
}
