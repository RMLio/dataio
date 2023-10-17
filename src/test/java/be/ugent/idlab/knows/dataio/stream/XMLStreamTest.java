package be.ugent.idlab.knows.dataio.stream;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.streams.XMLSourceStream;
import org.junit.Test;

public class XMLStreamTest extends StreamTestCore {
    @Test
    public void eval_0000() throws Exception {
        Access access = new LocalFileAccess("xml/0000.xml", "src/test/resources", "xml");
        try (XMLSourceStream stream = new XMLSourceStream(access, "students/student")) {
            runTest(stream, this::evaluate_0000);
        }
    }

    @Test
    public void eval_0001() throws Exception {
        Access access = new LocalFileAccess("xml/0001.xml", "src/test/resources", "xml");
        try (XMLSourceStream stream = new XMLSourceStream(access, "pubs/pub")) {
            runTest(stream, this::evaluate_0001);
        }
    }
}