package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.ExcelObservable;
import be.ugent.idlab.knows.dataio.source.ExcelSource;
import org.junit.jupiter.api.Test;

public class ExcelObservableTest extends ObservableTestCore {

    private void runExcelTest(String path, Evaluator evaluator) {
        Access access = new LocalFileAccess("", path, "xlsx");
        SourceObservable<ExcelSource> o = new ExcelObservable(access);
        runTest(o, evaluator);
    }

    @Test
    public void test_0000() {
        runExcelTest("src/test/resources/excel/0000.xlsx", this::evaluate_0000);
    }

    @Test
    public void test_0001() {
        runExcelTest("src/test/resources/excel/0001.xlsx", this::evaluate_0001);
    }

    @Test
    public void test_1001_header_long() {
        runExcelTest("src/test/resources/excel/1001_header_long.xlsx", this::evaluate_1001_header_long);
    }

    @Test
    public void test_1001_header_short() {
        runExcelTest("src/test/resources/excel/1001_header_short.xlsx", this::evaluate_1001_header_short);
    }
}
