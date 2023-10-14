package be.ugent.idlab.knows.dataio.iterator;

import be.ugent.idlab.knows.dataio.access.Access;
import be.ugent.idlab.knows.dataio.cores.TestCore;
import be.ugent.idlab.knows.dataio.exceptions.HeaderEmptyValuesException;
import be.ugent.idlab.knows.dataio.iterators.ODSSourceIterator;
import be.ugent.idlab.knows.dataio.record.ODSRecord;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ODSIteratorTest extends TestCore {

    @Test
    public void evaluate_0000_ods() throws SQLException, IOException {
        Access access = makeLocalAccess("/ods/0000.ods", "", "ods", "utf-8");
        try (ODSSourceIterator odsSourceIterator = new ODSSourceIterator(access)) {
            Assertions.assertTrue(evaluate_0000(odsSourceIterator));
        }
    }

    @Test
    public void evaluate_0001_ods() throws SQLException, IOException {
        Access access = makeLocalAccess("/ods/0001.ods", "", "ods", "utf-8");
        try (ODSSourceIterator odsSourceIterator = new ODSSourceIterator(access)) {
            Assertions.assertTrue(evaluate_0001(odsSourceIterator));
        }
    }

    @Test
    public void evaluate_1001_header_col_missing_CSV() {
        Access access = makeLocalAccess("/ods/1001_header_col_missing.ods", "", "ods", "utf-8");
        assertThrows(HeaderEmptyValuesException.class, () -> {
            ODSSourceIterator iterator = new ODSSourceIterator(access);
            iterator.close();
        });
    }

    @Test
    public void evaluate_1001_header_long_CSV() throws SQLException, IOException {
        Access access = makeLocalAccess("/ods/1001_header_long.ods", "", "ods", "utf-8");
        try (ODSSourceIterator odsSourceIterator = new ODSSourceIterator(access)) {
            Assertions.assertTrue(evaluate_1001_header_long(odsSourceIterator));
        }
    }

    @Test
    public void evaluate_1001_header_short_CSV() {
        Access access = makeLocalAccess("/ods/1001_header_short.ods", "", "ods", "utf-8");
        assertThrows(HeaderEmptyValuesException.class, () -> {
            ODSSourceIterator odsSourceIterator = new ODSSourceIterator(access);
            odsSourceIterator.close();
        });
    }

    @Test
    public void test_value_types() throws SQLException, IOException {
        Access access = makeLocalAccess("/ods/value_types.ods", "", "ods", "utf-8");
        try (ODSSourceIterator iterator = new ODSSourceIterator(access)) {
            ODSRecord record = (ODSRecord) iterator.next();

            assertEquals(record.getDataType("integer"), XSDDatatype.XSDinteger.getURI());
            assertEquals(record.getDataType("double"), XSDDatatype.XSDdouble.getURI());
            assertEquals(record.getDataType("string"), XSDDatatype.XSDstring.getURI());
            assertEquals(record.getDataType("boolean"), XSDDatatype.XSDboolean.getURI());
        }
    }
}
