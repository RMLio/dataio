package be.ugent.idlab.knows.dataio.cores;

import be.ugent.idlab.knows.dataio.streams.SourceStream;

public class StreamTestCore extends TestCore {
    protected void runTest(SourceStream stream, Evaluator evaluator) {
        evaluator.evaluate(stream.getStream().iterator());
    }
}
