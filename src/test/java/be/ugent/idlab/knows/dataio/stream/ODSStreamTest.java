package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.ODSSourceStream;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;

public class ODSStreamTest extends StreamTestCore {
    @Test
    public void eval_0000() throws XMLStreamException, SQLException, IOException {
        Access access = new LocalFileAccess("ods/0000.ods", "src/test/resources", "ods");
        try (ODSSourceStream stream = new ODSSourceStream(access)) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_0001() throws XMLStreamException, SQLException, IOException {
        Access access = new LocalFileAccess("ods/0001.ods", "src/test/resources", "ods");
        try (ODSSourceStream stream = new ODSSourceStream(access)) {
            runTest(stream, this::evaluate_0001);
        }
    }
}
