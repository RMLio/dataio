package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

public class XMLIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_XML() throws Exception {
        Access access = makeLocalAccess("/xml/0000.xml", "", "xml", StandardCharsets.UTF_8);
        try (XMLSourceIterator xmlSourceIterator = new XMLSourceIterator(access, "students/student")) {
            assertTrue(evaluate_0000(xmlSourceIterator));
        }
    }

    @Test
    public void evaluate_0001_XML() throws Exception {
        Access access = makeLocalAccess("/xml/0001.xml", "", "xml", StandardCharsets.UTF_8);
        try (XMLSourceIterator xmlSourceIterator = new XMLSourceIterator(access, "pubs/pub")) {
            assertTrue(evaluate_0001(xmlSourceIterator));
        }
    }

}
