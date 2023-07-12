package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.ODSObservable;
import be.ugent.idlab.knows.dataio.source.ODSSource;
import org.junit.jupiter.api.Test;

public class ODSObservableTest extends ObservableTestCore {
    private void runODSTest(String path, Evaluator evaluator) throws Exception {
        Access access = new LocalFileAccess("", path, "ods");
        try (SourceObservable<ODSSource> o = new ODSObservable(access)) {
            runTest(o, evaluator);
        }
    }

    @Test
    public void test_0000() throws Exception {
        runODSTest("src/test/resources/ods/0000.ods", this::evaluate_0000);
    }

    @Test
    public void test_0001() throws Exception {
        runODSTest("src/test/resources/ods/0001.ods", this::evaluate_0001);
    }
}
