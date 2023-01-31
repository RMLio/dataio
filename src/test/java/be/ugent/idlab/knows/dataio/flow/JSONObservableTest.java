package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.JSONObservable;
import be.ugent.idlab.knows.dataio.source.JSONSource;
import be.ugent.idlab.knows.dataio.source.Source;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class JSONObservableTest extends ObservableTestCore {
    private void runJSONTest(String path, TestCore.Evaluator evaluator, String iterator) {
        Access access = new LocalFileAccess("", path, "json");
        SourceObservable<JSONSource> o = new JSONObservable(access, iterator);

        runTest(o, evaluator);
    }

    @Test
    public void test_0000() {
        runJSONTest("src/test/resources/json/0000.json", this::evaluate_0000, "$.students[*]");
    }

    @Test
    public void test_0001() {
        runJSONTest("src/test/resources/json/0001.json", this::evaluate_0001, "$.pubs[*]");
    }
}
