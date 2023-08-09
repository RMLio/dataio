package be.ugent.idlab.knows.dataio.serializability;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.cores.ObservableTestCore;
import be.ugent.idlab.knows.dataio.flow.base.SourceObservable;
import be.ugent.idlab.knows.dataio.flow.observables.*;
import be.ugent.idlab.knows.dataio.iterators.csvw.CSVWConfiguration;
import be.ugent.idlab.knows.dataio.source.*;
import org.junit.jupiter.api.Test;

public class ObservableSerializabilityTest extends ObservableTestCore {

    private <T extends Source> void runSerializabilityTest(SourceObservable<T> observable) throws Exception {
        try (SourceObservable<T> o2 = simulateSerialization(observable)) {
            runTest(o2, this::evaluate_0001);
        }
    }

    @Test
    public void testCSVObservable() throws Exception {
        Access access = new LocalFileAccess("", "src/test/resources/csv/0001.csv", "csv");
        try (SourceObservable<CSVSource> o = new CSVObservable(access)) {
            runSerializabilityTest(o);
        }
    }

    @Test
    public void testCSVWObservable() throws Exception {
        Access access = new LocalFileAccess("", "src/test/resources/csv/0001.csv", "csvw");
        try (SourceObservable<CSVSource> o = new CSVWObservable(access, CSVWConfiguration.DEFAULT)) {
            runSerializabilityTest(o);
        }
    }

    @Test
    public void testExcelObservable() throws Exception {
        Access access = new LocalFileAccess("", "src/test/resources/excel/0001.xlsx", "xlsx");
        try (SourceObservable<ExcelSource> o = new ExcelObservable(access)) {
            runSerializabilityTest(o);
        }
    }

    @Test
    public void testJSONObservable() throws Exception {
        Access access = new LocalFileAccess("", "src/test/resources/json/0001.json", "json");
        try (SourceObservable<JSONSource> o = new JSONObservable(access, "$.pubs[*]")) {
            runSerializabilityTest(o);
        }
    }

    @Test
    public void testODSObservable() throws Exception {
        Access access = new LocalFileAccess("", "src/test/resources/ods/0001.ods", "ods");
        try (SourceObservable<ODSSource> o = new ODSObservable(access)) {
            runSerializabilityTest(o);
        }
    }

    @Test
    public void testXMLObservable() throws Exception {
        Access access = new LocalFileAccess("", "src/test/resources/xml/0001.xml", "xml");
        try (SourceObservable<XMLSource> o = new XMLObservable(access, "pubs/pub")) {
            runSerializabilityTest(o);
        }
    }
}
