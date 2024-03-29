package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.CSVWObservable;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.record.CSVRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class CSVWObservableTest extends ObservableTestCore {
    private void runCSVWTest(String path, Evaluator evaluator, CSVWConfiguration config) throws Exception {
        Access access = new LocalFileAccess("", path, "csv");
        try (SourceObservable<CSVRecord> o = new CSVWObservable(access, config)) {
            runTest(o, evaluator);
        }
    }

    @Test
    public void test_0000() throws Exception {
        runCSVWTest("src/test/resources/csv/0000.csv", this::evaluate_0000, CSVWConfiguration.DEFAULT);
    }

    @Test
    public void test_0001() throws Exception {
        runCSVWTest("src/test/resources/csv/0001.csv", this::evaluate_0001, CSVWConfiguration.DEFAULT);
    }

    @Test
    public void test_0000_trim() throws Exception {
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(true).build();
        runCSVWTest("src/test/resources/csvw/0000_trim.csv", this::evaluate_0000, config);
    }

    @Test
    public void test_1000_nulls() throws Exception { // CSVW specific test
        Access access = new LocalFileAccess("", "src/test/resources/csvw/1000_nulls.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder().withNulls(List.of("NULL")).build();

        String[] header = new String[]{"ID", "Name"};
        CSVRecord s1 = new CSVRecord(header, new String[]{"10", "Venus"}, access.getDataTypes()),
                s2 = new CSVRecord(header, new String[]{"12", "Serena"}, access.getDataTypes()),
                s3 = new CSVRecord(header, new String[]{"13", "null"}, access.getDataTypes()),
                s_null = new CSVRecord(header, new String[]{"11", null}, access.getDataTypes());


        try (SourceObservable<CSVRecord> o = new CSVWObservable(access, config)) {
            Assertions.assertTrue(compareIterator(getIteratorFromObservable(o), Set.of(s1, s2, s3, s_null)));
        }
    }

    @Test
    public void test_1001_header_long() throws Exception {
        runCSVWTest("src/test/resources/csv/1001_header_long.csv", this::evaluate_1001_header_long, CSVWConfiguration.DEFAULT);
    }

    @Test
    public void test_1001_header_short() throws Exception {
        runCSVWTest("src/test/resources/csv/1001_header_short.csv", this::evaluate_1001_header_short, CSVWConfiguration.DEFAULT);
    }
}
