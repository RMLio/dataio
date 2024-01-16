package be.ugent.idlab.knows.dataio.serializability;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.streams.*;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

public class StreamSerializabilityTest extends StreamTestCore {

    private void runSerializabilityTest(SourceStream stream) throws Exception {
        try (SourceStream s2 = simulateSerialization(stream)) {
            runTest(s2, this::evaluate_0001);
        }
    }

    @Test
    public void testCSVStream() throws Exception {
        Access access = new LocalFileAccess("csv/0001.csv", "src/test/resources", "csv", StandardCharsets.UTF_8);
        try (CSVSourceStream stream = new CSVSourceStream(access)) {
            runSerializabilityTest(stream);
        }
    }

    @Test
    public void testCSVWStream() throws Exception {
        Access access = new LocalFileAccess("csv/0001.csv", "src/test/resources", "csv");
        CSVWConfiguration config = CSVWConfiguration.DEFAULT;
        try (CSVWSourceStream stream = new CSVWSourceStream(access, config)) {
            runSerializabilityTest(stream);
        }
    }

    @Test
    public void testExcelStream() throws Exception {
        Access access = new LocalFileAccess("excel/0001.xlsx", "src/test/resources", "xlsx");
        try (ExcelSourceStream stream = new ExcelSourceStream(access)) {
            runSerializabilityTest(stream);
        }
    }

    @Test
    public void testJSONStream() throws Exception {
        Access access = new LocalFileAccess("json/0001.json", "src/test/resources", "json");
        try (JSONSourceStream stream = new JSONSourceStream(access, "$.pubs[*]")) {
            runSerializabilityTest(stream);
        }
    }

    @Test
    public void testODSStream() throws Exception {
        Access access = new LocalFileAccess("ods/0001.ods", "src/test/resources", "ods");
        try (ODSSourceStream stream = new ODSSourceStream(access)) {
            runSerializabilityTest(stream);
        }
    }

    @Test
    public void testXMLStream() throws Exception {
        Access access = new LocalFileAccess("xml/0001.xml", "src/test/resources", "xml");
        try (XMLSourceStream stream = new XMLSourceStream(access, "pubs/pub")) {
            runSerializabilityTest(stream);
        }
    }
}
