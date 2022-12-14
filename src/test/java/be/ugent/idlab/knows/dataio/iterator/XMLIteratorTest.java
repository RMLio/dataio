package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class XMLIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_XML() {
        XMLSourceIterator xmlSourceIterator = new XMLSourceIterator();
        xmlSourceIterator.open(makeLocalAccess("/xml/0000.xml", "", "xml", "utf-8"), "students/student");
        assertTrue(evaluate_0000(xmlSourceIterator));
    }

    @Test
    public void evaluate_0001_XML() {
        XMLSourceIterator xmlSourceIterator = new XMLSourceIterator();
        xmlSourceIterator.open(makeLocalAccess("/xml/0001.xml", "", "xml", "utf-8"), "pubs/pub");
        assertTrue(evaluate_0001(xmlSourceIterator));
    }

}
