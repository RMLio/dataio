package be.ugent.idlab.knows.dataio.records;

import be.ugent.idlab.knows.dataio.access.LocalFileAccess;
import be.ugent.idlab.knows.dataio.iterators.CSVSourceIterator;
import be.ugent.idlab.knows.dataio.record.Record;
import be.ugent.idlab.knows.dataio.record.RecordValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CSVRecordTest {

    @Test
    public void testExistingRecordNormalValue() throws Exception {
        try (CSVSourceIterator it = new CSVSourceIterator(new LocalFileAccess("0001.csv", "src/test/resources/csv"))) {
            Record result = it.next();
            RecordValue value = result.get("Name");
            assertTrue(value.isOk());
            assertEquals("Trollekelder", value.getValue());
        }
    }

    @Test
    public void testEmptyFile() throws Exception {
        try (CSVSourceIterator it = new CSVSourceIterator(new LocalFileAccess("empty.csv", "src/test/resources/csv"))) {
            it.next();
        } catch (NoSuchElementException e) {
            // This should fail
            return;
        }
        fail("NoSuchElementException expected");
    }

    @Test
    public void testEmptyStringNull() throws Exception {
        try (CSVSourceIterator it = new CSVSourceIterator(new LocalFileAccess("null_value.csv", "src/test/resources/csv"), Set.of(""))) {
            Record result = it.next();
            RecordValue value = result.get("Age");
            assertTrue(value.isEmpty());
        }
    }

    @Test
    public void testEmptyStringNonNull() throws Exception {
        try (CSVSourceIterator it = new CSVSourceIterator(new LocalFileAccess("null_value.csv", "src/test/resources/csv"))) {
            Record result = it.next();
            RecordValue value = result.get("Age");
            assertTrue(value.isOk());
            assertEquals("", value.getValue());
        }
    }

    @Test
    public void testEmptyCellNull() throws Exception {
        try (CSVSourceIterator it = new CSVSourceIterator(new LocalFileAccess("null_value.csv", "src/test/resources/csv"))) {
            it.next();  // Skip the first row, the second contains an empty cell
            Record result = it.next();
            RecordValue value = result.get("Name");
            assertTrue(value.isEmpty());
        }
    }

}
