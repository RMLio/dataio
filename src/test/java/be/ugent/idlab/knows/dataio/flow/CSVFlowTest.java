package be.ugent.idlab.knows.dataio.flow;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.flow.flowables.CSVFlowable;
import org.junit.Test;

public class CSVFlowTest {

    @Test
    public void test1() {
        Access access = new LocalFileAccess("src/test/resources/csv/0000.csv", "", "csv");
        CSVFlowable o = new CSVFlowable(access);
    }
}
