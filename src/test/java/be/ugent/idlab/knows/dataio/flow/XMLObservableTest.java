package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.XMLObservable;
import be.ugent.idlab.knows.dataio.source.XMLSource;
import org.junit.jupiter.api.Test;

public class XMLObservableTest extends ObservableTestCore {
    private void runXMLTest(String path, Evaluator evaluator, String xpath) throws Exception {
        Access access = new LocalFileAccess("", path, "xml");
        try (SourceObservable<XMLSource> o = new XMLObservable(access, xpath)) {
            runTest(o, evaluator);
        }
    }

    @Test
    public void test_0000() throws Exception {
        runXMLTest("src/test/resources/xml/0000.xml", this::evaluate_0000, "students/student");
    }

    @Test
    public void test_0001() throws Exception {
        runXMLTest("src/test/resources/xml/0001.xml", this::evaluate_0001, "pubs/pub");
    }
}
