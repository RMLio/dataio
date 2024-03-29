package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.CSVObservable;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.junit.jupiter.api.Test;

public class CSVObservableTest extends ObservableTestCore {
    private void runCSVTest(String path, TestCore.Evaluator evaluator) throws Exception {
        Access access = new LocalFileAccess("", path, "csv");
        try (SourceObservable<CSVRecord> o = new CSVObservable(access)) {
            runTest(o, evaluator);
        }
    }

    @Test
    public void test_0000() throws Exception {
        runCSVTest("src/test/resources/csv/0000.csv", this::evaluate_0000);
    }

    @Test
    public void test_0001() throws Exception {
        runCSVTest("src/test/resources/csv/0001.csv", this::evaluate_0001);
    }

    @Test
    public void test_1001_header_long() throws Exception {
        runCSVTest("src/test/resources/csv/1001_header_long.csv", this::evaluate_1001_header_long);
    }

    @Test
    public void test_1001_header_short() throws Exception {
        runCSVTest("src/test/resources/csv/1001_header_short.csv", this::evaluate_1001_header_short);
    }
}
