package be.ugent.idlab.knows.dataio.cores;

import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.source.Source;
import org.junit.jupiter.api.Assertions;

import java.util.Iterator;

public class ObservableTestCore extends TestCore {

    public Iterator<Source> getIteratorFromObservable(SourceObservable<? extends Source> o) {
        return o.toList().blockingGet().stream().map(s -> (Source) s).iterator();
    }

    public void runTest(SourceObservable<? extends Source> o, TestCore.Evaluator evaluator) {
        Iterator<Source> iter = getIteratorFromObservable(o);
        Assertions.assertTrue(evaluator.evaluate(iter));
    }
}
