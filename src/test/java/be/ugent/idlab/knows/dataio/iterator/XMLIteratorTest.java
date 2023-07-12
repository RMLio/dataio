package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.XMLSourceIterator;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

public class XMLIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_XML() throws SQLException, IOException, SaxonApiException {
        Access access = makeLocalAccess("/xml/0000.xml", "", "xml", "utf-8");
        try (XMLSourceIterator xmlSourceIterator = new XMLSourceIterator(access, "students/student")) {
            assertTrue(evaluate_0000(xmlSourceIterator));
        }
    }

    @Test
    public void evaluate_0001_XML() throws SQLException, IOException, SaxonApiException {
        Access access = makeLocalAccess("/xml/0001.xml", "", "xml", "utf-8");
        try (XMLSourceIterator xmlSourceIterator = new XMLSourceIterator(access, "pubs/pub")) {
            assertTrue(evaluate_0001(xmlSourceIterator));
        }
    }

}
