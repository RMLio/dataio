package be.ugent.idlab.knows.dataio.serializability;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.iterators.*;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IteratorSerializabilityTest extends TestCore {

    private void runSerializabilityTest(SourceIterator iterator) throws Exception { // exception arises from AutoClosable interface
        try (SourceIterator i2 = simulateSerialization(iterator)) {
            assertEquals(iterator.hasNext(), i2.hasNext());
            assertEquals(iterator.next(), i2.next());
        }
    }

    @Test
    public void testCSVIterator() throws Exception {
        Access access = new LocalFileAccess("csv/0001.csv", "src/test/resources", "csv");
        try (CSVSourceIterator iterator = new CSVSourceIterator(access)) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testCSVWIterator() throws Exception {
        Access access = new LocalFileAccess("csv/0001.csv", "src/test/resources", "csv");
        CSVWConfiguration config = CSVWConfiguration.DEFAULT;
        try (CSVWSourceIterator iterator = new CSVWSourceIterator(access, config)) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testExcelIterator() throws Exception {
        Access access = new LocalFileAccess("excel/0001.xlsx", "src/test/resources", "xlsx", StandardCharsets.UTF_8);
        try (ExcelSourceIterator iterator = new ExcelSourceIterator(access)) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testHTMLIterator() throws Exception {
        Access access = new LocalFileAccess("html/0001.html", "src/test/resources", "html", StandardCharsets.UTF_8);
        try (HTMLSourceIterator iterator = new HTMLSourceIterator(access, "table tbody tr")) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testJSONIterator() throws Exception {
        Access access = new LocalFileAccess("json/0001.json", "src/test/resources", "json", StandardCharsets.UTF_8);
        try (JSONSourceIterator iterator = new JSONSourceIterator(access, "$.pubs[*]")) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testJSONLinesIterator() throws Exception {
        Access access = new LocalFileAccess("json/data.jsonl", "src/test/resources", "jsonl", StandardCharsets.UTF_8);
        try (JSONLinesSourceIterator iterator = new JSONLinesSourceIterator(access, "$.*")) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testODSIterator() throws Exception {
        Access access = new LocalFileAccess("ods/0001.ods", "src/test/resources", "ods", StandardCharsets.UTF_8);
        try (ODSSourceIterator iterator = new ODSSourceIterator(access)) {
            runSerializabilityTest(iterator);
        }
    }

    @Test
    public void testXMLIterator() throws Exception {
        Access access = new LocalFileAccess("xml/0001.xml", "src/test/resources", "xml", StandardCharsets.UTF_8);
        try (XMLSourceIterator iterator = new XMLSourceIterator(access, "pubs/pub")) {
            runSerializabilityTest(iterator);
        }
    }
}
