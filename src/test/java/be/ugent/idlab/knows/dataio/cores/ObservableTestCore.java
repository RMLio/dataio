package be.ugent.idlab.knows.dataio.cores;

import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.record.Record;
import org.junit.jupiter.api.Assertions;

import java.util.Iterator;

public class ObservableTestCore extends TestCore {

    public Iterator<Record> getIteratorFromObservable(SourceObservable<? extends Record> o) {
        return o.toList().blockingGet().stream().map(s -> (Record) s).iterator();
    }

    public void runTest(SourceObservable<? extends Record> o, TestCore.Evaluator evaluator) {
        Iterator<Record> iter = getIteratorFromObservable(o);
        Assertions.assertTrue(evaluator.evaluate(iter));
    }
}
