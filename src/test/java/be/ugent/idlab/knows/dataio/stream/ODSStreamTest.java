package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.ODSSourceStream;
import org.junit.Test;

public class ODSStreamTest extends StreamTestCore {
    @Test
    public void eval_0000() {
        runTest("/ods/0000.ods", "src/test/resources", "ods", "utf-8", ODSSourceStream.class, this::evaluate_0000);
    }

    @Test
    public void eval_0001() {
        runTest("/ods/0001.ods", "src/test/resources", "ods", "utf-8", ODSSourceStream.class, this::evaluate_0001);
    }
}
