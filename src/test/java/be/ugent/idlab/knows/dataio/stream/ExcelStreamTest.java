package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.ExcelSourceStream;
import org.junit.Test;

public class ExcelStreamTest extends StreamTestCore {

    @Test
    public void eval_0000() throws Exception {
        Access access = new LocalFileAccess("excel/0000.xlsx", "src/test/resources", "xlsx");
        try (ExcelSourceStream stream = new ExcelSourceStream(access)) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_0001() throws Exception {
        Access access = new LocalFileAccess("excel/0001.xlsx", "src/test/resources", "xlsx");
        try (ExcelSourceStream stream = new ExcelSourceStream(access)) {
            runTest(stream, this::evaluate_0001);
        }
    }

    @Test
    public void eval_1001_header_long_excel() throws Exception {
        Access access = new LocalFileAccess("excel/1001_header_long.xlsx", "src/test/resources", "xlsx");

        try (ExcelSourceStream stream = new ExcelSourceStream(access)) {
            runTest(stream, this::evaluate_1001_header_long);
        }
    }

    @Test
    public void eval_1001_header_short_excel() throws Exception {
        Access access = new LocalFileAccess("excel/1001_header_short.xlsx", "src/test/resources", "xlsx");

        try (ExcelSourceStream stream = new ExcelSourceStream(access)) {
            runTest(stream, this::evaluate_1001_header_short);
        }
    }
}