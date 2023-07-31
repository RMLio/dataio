package be.ugent.idlab.knows.dataio.serializability;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.StreamTestCore;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.streams.*;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;

public class StreamSerializabilityTest extends StreamTestCore {
    @Test
    public void testCSVStream() throws SQLException, IOException {
        Access access = makeLocalAccess("/csv/0001.csv", "src/test/resources", "csv", "UTF-8");
        try (CSVSourceStream stream = new CSVSourceStream(access)) {
            CSVSourceStream s2 = simulateSerialization(stream);
            runTest(s2, this::evaluate_0001);
            s2.close();
        }
    }

    @Test
    public void testCSVWStream() throws SQLException, IOException {
        Access access = new LocalFileAccess("csvw/0000_trim.csv", "src/test/resources", "csv");
        CSVWConfiguration config = CSVWConfiguration.builder().withTrim(true).build();
        try (CSVWSourceStream stream = new CSVWSourceStream(access, config)) {
            CSVWSourceStream s2 = simulateSerialization(stream);
            runTest(s2, this::evaluate_0000);
            s2.close();
        }
    }

    @Test
    public void testExcelStream() throws SQLException, IOException {
        Access access = new LocalFileAccess("excel/0001.xlsx", "src/test/resources", "xlsx");
        try (ExcelSourceStream stream = new ExcelSourceStream(access)) {
            ExcelSourceStream s2 = simulateSerialization(stream);
            runTest(s2, this::evaluate_0001);
            s2.close();
        }
    }

    @Test
    public void testJSONStream() throws SQLException, IOException {
        Access access = new LocalFileAccess("json/0001.json", "src/test/resources", "json");
        try (JSONSourceStream stream = new JSONSourceStream(access, "$.pubs[*]")) {
            JSONSourceStream s2 = simulateSerialization(stream);
            runTest(s2, this::evaluate_0001);
            s2.close();
        }
    }

    @Test
    public void testODSStream() throws XMLStreamException, SQLException, IOException {
        Access access = new LocalFileAccess("ods/0001.ods", "src/test/resources", "ods");
        try (ODSSourceStream stream = new ODSSourceStream(access)) {
            ODSSourceStream s2 = simulateSerialization(stream);
            runTest(s2, this::evaluate_0001);
            s2.close();
        }
    }

    @Test
    public void testXMLStream() throws SQLException, IOException, SaxonApiException {
        Access access = new LocalFileAccess("xml/0001.xml", "src/test/resources", "xml");
        try (XMLSourceStream stream = new XMLSourceStream(access, "pubs/pub")) {
            XMLSourceStream s2 = simulateSerialization(stream);
            runTest(s2, this::evaluate_0001);
            s2.close();
        }
    }
}
