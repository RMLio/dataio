package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.JSONObservable;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import org.junit.jupiter.api.Test;

public class JSONObservableTest extends ObservableTestCore {
    private void runJSONTest(String path, TestCore.Evaluator evaluator, String iterator) throws Exception {
        Access access = new LocalFileAccess("", path, "json");
        try (SourceObservable<JSONSource> o = new JSONObservable(access, iterator)) {
            runTest(o, evaluator);
        }

    }

    @Test
    public void test_0000() throws Exception {
        runJSONTest("src/test/resources/json/0000.json", this::evaluate_0000, "$.students[*]");
    }

    @Test
    public void test_0001() throws Exception {
        runJSONTest("src/test/resources/json/0001.json", this::evaluate_0001, "$.pubs[*]");
    }
}
