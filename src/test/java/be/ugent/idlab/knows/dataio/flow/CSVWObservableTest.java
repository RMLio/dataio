package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.CSVWObservable;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import com.opencsv.CSVParserBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class CSVWObservableTest extends ObservableTestCore {
    private void runCSVWTest(String path, Evaluator evaluator, CSVParserBuilder parser, List<String> nulls, boolean skipHeader, boolean trim) {
        Access access = new LocalFileAccess("", path, "csv");
        SourceObservable<CSVSource> o = new CSVWObservable(access, parser, nulls, skipHeader, trim);
        runTest(o, evaluator);
    }

    @Test
    public void test_0000() {
        runCSVWTest("src/test/resources/csv/0000.csv", this::evaluate_0000, null, List.of(), false, false);
    }

    @Test
    public void test_0001() {
        runCSVWTest("src/test/resources/csv/0001.csv", this::evaluate_0001, null, List.of(), false, false);
    }

    @Test
    public void test_0000_trim() {
        runCSVWTest("src/test/resources/csvw/0000_trim.csv", this::evaluate_0000, null, List.of(), false, true);
    }

    @Test
    public void test_1000_nulls() { // CSVW specific test
        Access access = new LocalFileAccess("", "src/test/resources/csvw/1000_nulls.csv", "csv");
        SourceObservable<CSVSource> o = new CSVWObservable(access, null, List.of("NULL"), false, false);

        String[] header = new String[]{"ID", "Name"};
        CSVSource s1 = new CSVSource(header, new String[]{"10", "Venus"}, access.getDataTypes()),
                s2 = new CSVSource(header, new String[]{"12", "Serena"}, access.getDataTypes()),
                s3 = new CSVSource(header, new String[]{"13", "null"}, access.getDataTypes()),
                s_null = new CSVSource(header, new String[]{"11", null}, access.getDataTypes());

        Assertions.assertTrue(compareIterator(getIteratorFromObservable(o), Set.of(s1, s2, s3, s_null)));
    }

    @Test
    public void test_1001_header_long() {
        runCSVWTest("src/test/resources/csv/1001_header_long.csv", this::evaluate_1001_header_long, null, List.of(), false, false);
    }

    @Test
    public void test_1001_header_short() {
        runCSVWTest("src/test/resources/csv/1001_header_short.csv", this::evaluate_1001_header_short, null, List.of(), false, false);
    }
}
