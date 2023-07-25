package be.ugent.idlab.knows.dataio.serializability;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.*;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.CSVSource;
import net.sf.saxon.s9api.SaxonApiException;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IteratorSerializabilityTest extends TestCore {
    @Test
    public void testCSVIterator() throws SQLException, IOException {
        Access access = new LocalFileAccess("csv/0001.csv", "src/test/resources", "csv");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            byte[] bytes = SerializationUtils.serialize(iterator);
            CSVSourceIterator i2 = SerializationUtils.deserialize(bytes);

            assertTrue(evaluate_0001(i2));
            i2.close();
        }
    }

    @Test
    public void testCSVWIterator() throws SQLException, IOException {
        Access access = new LocalFileAccess("csvw/mapper/delimiter.csv", "src/test/resources", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder().withDelimiter(';').build();
        try (CSVWSourceIterator iterator = new CSVWSourceIterator(access, config)) {
            byte[] bytes = SerializationUtils.serialize(iterator);
            CSVWSourceIterator i2 = SerializationUtils.deserialize(bytes);

            CSVSource expected = new CSVSource(new String[]{"ID", "Name"}, new String[]{"10", "Venus"}, access.getDataTypes());
            assertEquals(expected, i2.next());

            i2.close();
        }
    }

    @Test
    public void testExcelIterator() throws SQLException, IOException {
        Access access = makeLocalAccess("/excel/0001.xlsx", "", "xlsx", "utf-8");
        try (ExcelSourceIterator iterator = new ExcelSourceIterator(access)) {
            byte[] bytes = SerializationUtils.serialize(iterator);
            ExcelSourceIterator i2 = SerializationUtils.deserialize(bytes);

            assertTrue(evaluate_0001(i2));

            i2.close();
        }
    }

    @Test
    public void testHTMLIterator() throws SQLException, IOException {
        Access access = makeLocalAccess("/html/0001.html", "", "html", "utf-8");
        try (HTMLSourceIterator iterator = new HTMLSourceIterator(access, "table tbody tr")) {
            byte[] bytes = SerializationUtils.serialize(iterator);
            HTMLSourceIterator i2 = SerializationUtils.deserialize(bytes);

            assertTrue(evaluate_0001(i2));

            i2.close();
        }
    }

    @Test
    public void testJSONIterator() throws SQLException, IOException {
        Access access = makeLocalAccess("/json/0001.json", "", "json", "utf-8");
        JSONSourceIterator iterator = new JSONSourceIterator(access, "$.pubs[*]");
        assertTrue(evaluate_0001(iterator));

        byte[] bytes = SerializationUtils.serialize(iterator);
        JSONSourceIterator i2 = SerializationUtils.deserialize(bytes);

        assertTrue(evaluate_0001(i2));
    }

    @Test
    public void testODSIterator() throws XMLStreamException, SQLException, IOException {
        Access access = makeLocalAccess("/ods/0001.ods", "", "ods", "utf-8");
        try (ODSSourceIterator iterator = new ODSSourceIterator(access)) {

            byte[] bytes = SerializationUtils.serialize(iterator);
            ODSSourceIterator i2 = SerializationUtils.deserialize(bytes);

            assertTrue(evaluate_0001(i2));

            i2.close();
        }
    }

    @Test
    public void testXMLIterator() throws SQLException, IOException, SaxonApiException {
        Access access = makeLocalAccess("/xml/0001.xml", "", "xml", "utf-8");
        try (XMLSourceIterator iterator = new XMLSourceIterator(access, "pubs/pub")) {
            byte[] bytes = SerializationUtils.serialize(iterator);
            XMLSourceIterator i2 = SerializationUtils.deserialize(bytes);

            assertTrue(evaluate_0001(i2));

            i2.close();
        }
    }
}
