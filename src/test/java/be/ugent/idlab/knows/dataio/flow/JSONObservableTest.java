package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.JSONObservable;
import be.ugent.idlab.knows.dataio.record.JSONRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JSONObservableTest extends ObservableTestCore {
    private void runJSONTest(String path, TestCore.Evaluator evaluator, String iterator) throws Exception {
        Access access = new LocalFileAccess("", path, "json");
        try (SourceObservable<JSONRecord> o = new JSONObservable(access, iterator)) {
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

    @Test
    public void testMagicPropertyPath() throws Exception {
        Access access = new LocalFileAccess("json/people.json", "src/test/resources", "json");
        try (JSONObservable observable = new JSONObservable(access, "$.people[*]")) {
            JSONRecord source = observable.blockingSingle();
            // grab the whole path
            Assertions.assertEquals("[0,people]", source.get("\\_PATH").getValue());
            // index the path
            Assertions.assertEquals("people", source.get("\\_PATH[1]").getValue());
        }
    }
}
