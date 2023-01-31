package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.CSVWObservable;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import com.opencsv.CSVParserBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class CSVWObservableTest extends ObservableTestCore {
    private void runCSVWTest(String path, Evaluator evaluator, CSVWConfiguration config) {
        Access access = new LocalFileAccess("", path, "csv");
        SourceObservable<CSVSource> o = new CSVWObservable(access, config);
        runTest(o, evaluator);
    }

    @Test
    public void test_0000() {
        runCSVWTest("src/test/resources/csv/0000.csv", this::evaluate_0000, CSVWConfiguration.DEFAULT);
    }

    @Test
    public void test_0001() {
        runCSVWTest("src/test/resources/csv/0001.csv", this::evaluate_0001, CSVWConfiguration.DEFAULT);
    }

    @Test
    public void test_0000_trim() {
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(true).build();
        runCSVWTest("src/test/resources/csvw/0000_trim.csv", this::evaluate_0000, config);
    }

    @Test
    public void test_1000_nulls() { // CSVW specific test
        Access access = new LocalFileAccess("", "src/test/resources/csvw/1000_nulls.csv", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder().withNulls(List.of("NULL")).build();
        SourceObservable<CSVSource> o = new CSVWObservable(access, config);

        String[] header = new String[]{"ID", "Name"};
        CSVSource s1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes()),
                s2 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes()),
                s3 = new CSVSource(header, new String[]{"13", "null"}, access.getDataTypes()),
                s_null = new CSVSource(header, new String[]{"11", null}, access.getDataTypes());

        Assertions.assertTrue(compareIterator(getIteratorFromObservable(o), Set.of(s1, s2, s3, s_null)));
    }

    @Test
    public void test_1001_header_long() {
        runCSVWTest("src/test/resources/csv/1001_header_long.csv", this::evaluate_1001_header_long, CSVWConfiguration.DEFAULT);
    }

    @Test
    public void test_1001_header_short() {
        runCSVWTest("src/test/resources/csv/1001_header_short.csv", this::evaluate_1001_header_short, CSVWConfiguration.DEFAULT);
    }
}
